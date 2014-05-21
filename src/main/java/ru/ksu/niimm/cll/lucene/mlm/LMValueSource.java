package ru.ksu.niimm.cll.lucene.mlm;

import org.apache.lucene.index.*;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.TermFreqValueSource;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.Map;

/**
 * @author Nikita Zhiltsov
 */
public class LMValueSource extends TermFreqValueSource {
    public LMValueSource(String field, String val, String indexedField, BytesRef indexedBytes) {
        super(field, val, indexedField, indexedBytes);
    }

    @Override
    public String name() {
        return "lmd";
    }

    @Override
    public String description() {
        return name() + '(' + field.toUpperCase() + ", '" + new String(indexedBytes.bytes, indexedBytes.offset, indexedBytes.length) + "')";
    }

    @Override
    public FunctionValues getValues(Map context, final AtomicReaderContext readerContext) throws IOException {
        Fields fields = readerContext.reader().fields();
        final Terms terms = fields.terms(indexedField);
        final IndexSearcher searcher = (IndexSearcher) context.get("searcher");
        final IndexReaderContext topContext = searcher.getTopReaderContext();
        final Term term = new Term(indexedField, indexedBytes);
        final TermContext termContext = TermContext.build(topContext, term);
        final CollectionStatistics collectionStats = searcher.collectionStatistics(indexedField);
        final LMPerFieldDirichletSimilarity dirichletSimilarity = (LMPerFieldDirichletSimilarity) searcher.getSimilarity();
        final BasicStats basicStats = dirichletSimilarity.newStats(indexedField, 1);
        final TermStatistics termStats =
                new TermStatistics(term.bytes(), termContext.docFreq(), termContext.totalTermFreq());
        dirichletSimilarity.fillBasicStats(basicStats, collectionStats, termStats);
        final NumericDocValues norms = readerContext.reader().getNormValues(indexedField);
        if (dirichletSimilarity == null) {
            throw new UnsupportedOperationException("requires an LMPerFieldDirichletSimilarity");
        }

        return new FloatDocValues(this) {
            DocsEnum docs;
            int atDoc;
            int lastDocRequested = -1;

            {
                reset();
            }

            public void reset() throws IOException {
                // no one should call us for deleted docs?

                if (terms != null) {
                    final TermsEnum termsEnum = terms.iterator(null);
                    if (termsEnum.seekExact(indexedBytes)) {
                        docs = termsEnum.docs(null, null);
                    } else {
                        docs = null;
                    }
                } else {
                    docs = null;
                }

                if (docs == null) {
                    docs = new DocsEnum() {
                        @Override
                        public long cost() {
                            return 0;
                        }

                        @Override
                        public int freq() {
                            return 0;
                        }

                        @Override
                        public int docID() {
                            return DocIdSetIterator.NO_MORE_DOCS;
                        }

                        @Override
                        public int nextDoc() {
                            return DocIdSetIterator.NO_MORE_DOCS;
                        }

                        @Override
                        public int advance(int target) {
                            return DocIdSetIterator.NO_MORE_DOCS;
                        }
                    };
                }
                atDoc = -1;
            }

            @Override
            public float floatVal(int doc) {
                try {
                    if (doc < lastDocRequested) {
                        // out-of-order access.... reset
                        reset();
                    }
                    lastDocRequested = doc;

                    if (atDoc < doc) {
                        atDoc = docs.advance(doc);
                    }

                    if (atDoc > doc) {
                        // term doesn't match this document... either because we hit the
                        // end, or because the next doc is after this doc.
                        return computeScore(0, doc);
                    }

                    // a match!
                    return computeScore(docs.freq(), docs.docID());
                } catch (IOException e) {
                    throw new RuntimeException("caught exception in function " + description() + " : doc=" + doc, e);
                }
            }

            private float computeScore(int freq, int docId) throws IOException {
                Long norm = new Long(norms.get(docId));
                return dirichletSimilarity.score(basicStats, freq, dirichletSimilarity.decodeNormValue(norm.byteValue()));
            }
        };
    }
}
