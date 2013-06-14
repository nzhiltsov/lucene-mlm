package ru.ksu.niimm.cll.lucene.mlm;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

/**
 * The searcher with a mixture of language models based ranking
 *
 * @author Nikita Zhiltsov
 */
public class MLMSearcher extends IndexSearcher {
    public MLMSearcher(IndexReader indexReader) {
        super(indexReader);
        setSimilarity(new LMPerFieldDirichletSimilarity());
    }
}
