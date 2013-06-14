package ru.ksu.niimm.cll.lucene.mlm;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.Norm;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;

/**
 * Similarity that supports per-field Dirichlet smoothing
 *
 * @author Nikita Zhiltsov
 */
public class LMPerFieldDirichletSimilarity extends LMDirichletSimilarity {

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        float mu = stats.getAvgFieldLength();
        float collectionProbability = ((LMStats) stats).getCollectionProbability();
        float score = (freq + mu * collectionProbability) / (docLen + mu);
        return score;
    }

    @Override
    public void computeNorm(FieldInvertState state, Norm norm) {
        super.computeNorm(state, norm);
    }

    @Override
    protected float decodeNormValue(byte norm) {
        return super.decodeNormValue(norm);
    }

    @Override
    protected void fillBasicStats(BasicStats stats, CollectionStatistics collectionStats, TermStatistics termStats) {
        super.fillBasicStats(stats, collectionStats, termStats);
    }

    @Override
    public BasicStats newStats(String field, float queryBoost) {
        return super.newStats(field, queryBoost);
    }
}
