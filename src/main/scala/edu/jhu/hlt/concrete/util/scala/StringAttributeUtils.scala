/* 
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved. 
 * This software is released under the 2-clause BSD license. See LICENSE in the project root directory. 
 */

package edu.jhu.hlt.concrete.util.scala

import edu.jhu.hlt.concrete.Graph.{StringAttribute, LabeledAttribute}
import edu.jhu.hlt.concrete.Graph.{Vertex, VertexKindAttribute}

/**
 * @author John Sullivan
 */
trait StringAttributeUtils {

  def self:(String, Float)

  val (value, confidence) = self

  def asStringAttribute:StringAttribute = StringAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(confidence.asAnnotationMeta)
    .setValue(value)
    .build

  def asLabeledAttribute:LabeledAttribute = LabeledAttribute.newBuilder
    .setUuid(genUUID)
    .setMetadata(confidence.asAnnotationMeta)
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
    .setMetadata(confidence.asAnnotationMeta)
    .setValue(kind)
    .build
}
