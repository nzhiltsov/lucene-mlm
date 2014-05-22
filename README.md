Lucene-MLM
=================

A mixture of language models ranker for Lucene
------------------------------
Lucene-MLM is a tiny Java library enabling a mixture of language models [1] as a ranker for Lucene 4.

[1] P. Ogilvie, J. Callan. Combining Document Representations for Known-Item Search. SIGIR'03 (2003).

Current Version
------------
[0.2.2](https://github.com/nzhiltsov/lucene-mlm/archive/0.2.2.zip)

Features
------------
* The library is built atop the Lucene 4 API (FunctionQuery, ValueSource classes)
* Correct computation of Dirichlet priors for terms*
    
*) the standard implementation of LMSimilarity wrongly retrieves only documents containing all the query terms and assumes non-matched terms as of zero priors

Known Issues
------------
Slower speed performance than that of standard rankers, due to matching against all the query terms (OR semantics). There is a need of top-k pre-ranking for better performance. 

Prerequisites
----------------------
* Java 1.6+
* Scala 2.9.2+ (optional, used only for tests)
* Tested on Lucene 4.8
