package ru.ksu.niimm.cll.lucene.mlm

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import java.util
import scala.collection.JavaConversions._

/**
 * @author Nikita Zhiltsov 
 */
@RunWith(classOf[JUnitSuiteRunner])
class MLMQueryBuilderTest extends JUnit4(MLMQueryBuilderTestSpec)

object  MLMQueryBuilderTestSpec extends Specification {
  "The builder" should {
    val fieldWeights = new util.HashMap[String, java.lang.Float]
    fieldWeights.put("name", 0.25f)
    fieldWeights.put("attributes", 0.25f)
    fieldWeights.put("outgoingLinks", 0.25f)
    fieldWeights.put("incomingLinks", 0.25f)
    val queryBuilder = new MLMQueryBuilder(fieldWeights)

      "build a query" in {
        val termList = List("rna", "dna")
        val q = queryBuilder.build(termList)
        q.toString() mustEqual termList.map {
          t: String => Array("outgoingLinks", "name", "incomingLinks", "attributes").map {
            f: String =>
              "lmd(".concat(f.toUpperCase).concat(", '").concat(t).concat("')")
          }.mkString("weightedsum(", ",", ")")
        }.mkString("product(", ",", ")")
    }
  }
}
