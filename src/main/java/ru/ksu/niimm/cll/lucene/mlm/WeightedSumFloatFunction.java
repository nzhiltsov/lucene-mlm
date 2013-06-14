package ru.ksu.niimm.cll.lucene.mlm;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.SumFloatFunction;

/**
 * @author Nikita Zhiltsov
 */
public class WeightedSumFloatFunction extends SumFloatFunction {
    private final float[] weights;

    public WeightedSumFloatFunction(ValueSource[] sources, float[] weights) {
        super(sources);
        if (sources.length != weights.length)
            throw new RuntimeException("The lengths of the source and weight vector must be the same.");
        this.weights = weights;
    }

    @Override
    protected String name() {
        return "weightedsum";
    }

    @Override
    protected float func(int doc, FunctionValues[] valsArr) {
        float val = 0.0f;
        int i = 0;
        for (FunctionValues vals : valsArr) {
            val += weights[i] * vals.floatVal(doc);
        }
        return val;
    }
}
