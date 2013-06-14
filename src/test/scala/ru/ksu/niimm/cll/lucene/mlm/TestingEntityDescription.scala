package ru.ksu.niimm.cll.lucene.mlm

/**
 * Testing entity description in terms of the SEM approach
 *
 * @author Nikita Zhiltsov
 */
class TestingEntityDescription(_entityURI: String, _namePredicate: String, _attributesPredicate: String,
                               _outRelations: String, _inRelations: String) {
  def entityURI = _entityURI

  def namePredicate = _namePredicate

  def attributesPredicate = _attributesPredicate

  def outRelations = _outRelations

  def inRelations = _inRelations
}
