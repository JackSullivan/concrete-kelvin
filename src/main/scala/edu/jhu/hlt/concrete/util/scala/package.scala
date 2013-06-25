package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.Concrete.{EdgeId, UUID => ConUUID}

/**
 * @author John Sullivan
 */
package object scala {
  def genUUID:ConUUID = ValUUID.random.asConcrete

  implicit def OrderedUUIDTup(_thisTup:(ValUUID, ValUUID)) = new UUIDTupleUtils {def thisTup = _thisTup}
  implicit def MetadataUtil(_self:Float) = new MetadataUtils {def self: Float = _self}
  implicit def StringAttrUtil(_tup:(String, Float)) = new StringAttributeUtils {def self: (String, Float) = _tup}
  implicit def TupleStringAttrUtil(_tup:(String, String, Float)) = new TupleStringAttributeUtils {def self: (String, String, Float) = _tup}
}
