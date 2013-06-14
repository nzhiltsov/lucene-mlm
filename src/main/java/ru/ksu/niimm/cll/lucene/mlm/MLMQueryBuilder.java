package ru.ksu.niimm.cll.lucene.mlm;

import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ProductFloatFunction;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;

import java.util.*;

/**
 * @author Nikita Zhiltsov
 */
public class MLMQueryBuilder {
    private final Map<String, Float> fieldWeights;

    public MLMQueryBuilder(Map<String, Float> fieldWeights) {
        this.fieldWeights = new HashMap<String, Float>();
        Set<Map.Entry<String, Float>> entries = fieldWeights.entrySet();
        for (Map.Entry<String, Float> fieldWeight : entries) {
            if (fieldWeight.getValue() != 0) {
                this.fieldWeights.put(fieldWeight.getKey(), fieldWeight.getValue());
            }
        }
    }

    public Query build(List<String> queryTerms) {
        ValueSource[] firstSources = new ValueSource[queryTerms.size()];
        int i = 0;
        List<Map.Entry<String, Float>> fieldWeightsList = new LinkedList<Map.Entry<String, Float>>(fieldWeights.entrySet());
        float[] weights = new float[fieldWeightsList.size()];
        int k = 0;
        for (Map.Entry<String, Float> entry : fieldWeightsList) {
            weights[k] = entry.getValue();
        }
        for (String queryTerm : queryTerms) {
            ValueSource[] secondSources = new ValueSource[fieldWeights.size()];
            int j = 0;
            for (Map.Entry<String, Float> fieldWeight : fieldWeightsList) {
                String field = fieldWeight.getKey();
                secondSources[j] = new LMValueSource(field, field, field, new BytesRef(queryTerm));
                j++;
            }
            firstSources[i] = new WeightedSumFloatFunction(secondSources, weights);
            i++;
        }
       return new FunctionQuery(new ProductFloatFunction(firstSources));
    }
}
