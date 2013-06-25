package edu.jhu.hlt.concrete.util.scala

import edu.jhu.hlt.concrete.Concrete.{AttributeMetadata, AnnotationMetadata}

/**
 * @author John Sullivan
 */
trait MetadataUtils {

  def self:Float

  def asAttributeMeta:AttributeMetadata = AttributeMetadata.newBuilder
    .setConfidence(self)
    .setTool(Settings.Concrete.attributeMetadata)
    .build

  def asAnnotationMeta:AnnotationMetadata = AnnotationMetadata.newBuilder
    .setConfidence(self)
    .setTool(Settings.Concrete.annotationMetadata)
    .build
}
