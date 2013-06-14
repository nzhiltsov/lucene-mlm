Lucene-MLM
=================

A mixture of language models ranker for Lucene
------------------------------
Lucene-MLM is a tiny Java library enabling a mixture of language models [1] as a ranker for Lucene 4.0.

[1] P. Ogilvie, J. Callan. Combining Document Representations for Known-Item Search. SIGIR'03 (2003).

Current Version
------------
[0.1](https://github.com/nzhiltsov/lucene-mlm/archive/0.1.zip)

Features
------------
* The library is built atop the Lucene 4.0 API (FunctionQuery, ValueSource classes)
* Correct computation of Dirichlet priors for terms*
    
-* the standard implementation of LMSimilarity retrieve only documents containing all the query terms and treats non-matched terms as of zero priors 

Prerequisites
----------------------
* Java 1.6+
* Scala 2.9.2+ (optional, used only for tests)
