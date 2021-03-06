/* 
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved. 
 * This software is released under the 2-clause BSD license. See LICENSE in the project root directory. 
 */

package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.Concrete.{UUID => ConUUID}
import edu.jhu.hlt.concrete.Graph.{EdgeId}

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
