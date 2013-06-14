package ru.ksu.niimm.cll.lucene.mlm

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.util.Version
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.index.{FieldInfo, IndexWriter, ConcurrentMergeScheduler, IndexWriterConfig}
import org.apache.lucene.document.{Field, StringField, Document, FieldType}
import org.slf4j.LoggerFactory

/**
 * @author Nikita Zhiltsov 
 */
class TestingSEMIndexer {
  private val logger = LoggerFactory.getLogger("lucene-mlm.TestingSEMIndexer")

  private val analyzer = new StandardAnalyzer(Version.LUCENE_40)

  val directory = new RAMDirectory

  private val indexWriterConfig = {
    val config: IndexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, analyzer)
    config.setSimilarity(new LMPerFieldDirichletSimilarity)
    config.setRAMBufferSizeMB(256)
    config.setMaxBufferedDocs(IndexWriterConfig.DISABLE_AUTO_FLUSH)
    config.setMaxBufferedDeleteTerms(IndexWriterConfig.DISABLE_AUTO_FLUSH)
    val mergeScheduler: ConcurrentMergeScheduler = config.getMergeScheduler.asInstanceOf[ConcurrentMergeScheduler]
    mergeScheduler.setMaxMergeCount(8)
    mergeScheduler.setMaxThreadCount(6)
    config
  }

  val writer = new IndexWriter(directory, indexWriterConfig)

  private val indexedFieldType = {
    val fieldType = new FieldType
    fieldType.setIndexed(true)
    fieldType.setStoreTermVectors(true)
    fieldType.setStoreTermVectorPositions(true)
    fieldType.setTokenized(true)
    fieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
    fieldType.freeze
    fieldType
  }

  private var docCounter: Int = 0

  private val beforeCommit = 10

  def addDocument(entityURI: String,
                  namePredicate: String,
                  attributesPredicate: String,
                  outRelations: String,
                  inRelations: String) {
    val doc = new Document
    doc.add(new StringField("uri", entityURI, Field.Store.YES))
    doc.add(new Field("name", namePredicate, indexedFieldType))
    doc.add(new Field("attributes", attributesPredicate, indexedFieldType))
    doc.add(new Field("outgoingLinks", outRelations, indexedFieldType))
    doc.add(new Field("incomingLinks", inRelations, indexedFieldType))
    writer.addDocument(doc)
    commit(true, entityURI)
  }

  def commit = {
    writer.commit
  }
  /**
   * commit the entities in the writer buffer
   *
   * @param indexing if true, readAndCheck if the buffer is full, otherwise, if false, commit without checking
   * @param subject  last committed entity URI
   */
  private def commit(indexing: Boolean, subject: String) = {
    docCounter += 1
    if (!indexing || (docCounter % beforeCommit) == 0) {
      writer.commit();
      if (indexing)
        logger.info("Committed {} entities. The last entity URI: {}", beforeCommit, subject)
      else
        logger.info("Committed {} entities.", docCounter)
    }
  }

  /**
   * close the indexer and release underlying resources
   */
  def close = {
    commit(false, null)
    writer.close
    directory.close
    logger.info("Finished indexing.")
  }
}
