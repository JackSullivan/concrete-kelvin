package edu.jhu.hlt.concrete.util.scala

import edu.jhu.hlt.concrete.Graph.LabeledAttribute

/**
 * @author John Sullivan
 */
trait TupleStringAttributeUtils {
  def self:(String, String, Float)

  val (label, value, confidence) = self

  def asLabeledAttribute:LabeledAttribute = LabeledAttribute.newBuilder
    .setUuid(genUUID)
    .setLabel(label)
    .setValue(value)
    .setMetadata(confidence.asAnnotationMeta)
    .build
}
