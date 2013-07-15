/* 
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved. 
 * This software is released under the 2-clause BSD license. See LICENSE in the project root directory. 
 */

package edu.jhu.hlt.concrete.util.scala

import edu.jhu.hlt.concrete.Concrete.{AnnotationMetadata}

/**
 * @author John Sullivan
 */
trait MetadataUtils {

  def self:Float

  def asAnnotationMeta:AnnotationMetadata = AnnotationMetadata.newBuilder
    .setConfidence(self)
    .setTool(Settings.Concrete.annotationMetadata)
    .build
}
