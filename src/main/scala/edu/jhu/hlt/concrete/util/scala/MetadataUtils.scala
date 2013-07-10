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
