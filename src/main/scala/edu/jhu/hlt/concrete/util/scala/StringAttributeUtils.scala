package edu.jhu.hlt.concrete.util.scala

import edu.jhu.hlt.concrete.Concrete.{Vertex, StringAttribute, LabeledAttribute, VertexKindAttribute}

/**
 * @author John Sullivan
 */
trait StringAttributeUtils {

  def self:(String, Float)

  val (value, confidence) = self

  def asStringAttribute:StringAttribute = StringAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(confidence.asAttributeMeta)
    .setValue(value)
    .build

  def asLabeledAttribute:LabeledAttribute = LabeledAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(confidence.asAttributeMeta)
    .setLabel(value)
    .build

  private def kind:Vertex.Kind = value match {
    case "ORG" => Vertex.Kind.ORGANIZATION
    case "PER" => Vertex.Kind.PERSON
    case "GPE" => Vertex.Kind.GPE
    case _ => Vertex.Kind.UNKNOWN // We shouldn't get here
  }

  def asVertexKindAttribute:VertexKindAttribute = VertexKindAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(confidence.asAttributeMeta)
    .setValue(kind)
    .build
}
