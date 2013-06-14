package ru.ksu.niimm.cll.lucene.mlm

import org.junit.runner.RunWith
import org.specs.Specification
import org.specs.specification.BeforeAfter
import org.apache.lucene.index.DirectoryReader
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import scala.collection.JavaConversions._
import java.util

/**
 * @author Nikita Zhiltsov
 */
@RunWith(classOf[JUnitSuiteRunner])
class MLMSearcherTest extends JUnit4(MLMSearcherTestSpec)

object MLMSearcherTestSpec extends Specification with BeforeAfter {
  val indexer = new TestingSEMIndexer
  val entities = List(new TestingEntityDescription("http://eprints.rkbexplorer.com/id/caltech/eprints-7519",
    "rna dna rna",
    "rna virus",
    "cell immune",
    ""
  ),
    new TestingEntityDescription("http://eprints.rkbexplorer.com/id/caltech/eprints-7520",
      "rna dna dna",
      "",
      "result rna cell",
      ""
    ))

  doBeforeSpec {
    entities.foreach((e: TestingEntityDescription) =>
      indexer.addDocument(e.entityURI, e.namePredicate, e.attributesPredicate, e.outRelations, e.inRelations))
    indexer.commit
  }
  doAfterSpec {
    indexer.close
  }

  "The searcher" should {
    val reader = DirectoryReader.open(indexer.directory)
    val searcher = new MLMSearcher(reader)
    "retrieve relevant entities" in {
      {
        val fieldWeights = new util.HashMap[String, java.lang.Float]
        fieldWeights.put("name", 0.25f)
        fieldWeights.put("attributes", 0.25f)
        fieldWeights.put("outgoingLinks", 0.25f)
        fieldWeights.put("incomingLinks", 0.25f)
        val queryBuilder = new MLMQueryBuilder(fieldWeights)
        val query = queryBuilder.build(List("rna", "dna"))
        val docs = searcher.search(query, 10).scoreDocs
        docs.length must_== 2
        docs(0).doc must_== 1
        docs(1).doc must_== 0
        docs(0).score must be equalTo (0.28155208f)
        docs(1).score must be equalTo (0.21144289f)
      }
    }

  }

}
