/* 
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved. 
 * This software is released under the 2-clause BSD license. See LICENSE in the project root directory. 
 */

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
