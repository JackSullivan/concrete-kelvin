package edu.jhu.hlt.concrete.util.scala

import edu.jhu.hlt.concrete.Graph.EdgeId

/**
 * @author John Sullivan
 * mostly relevant to EdgeId creation and management
 */
trait UUIDTupleUtils extends Ordered[(ValUUID, ValUUID)] {
  def thisTup:(ValUUID, ValUUID)

  def compare(thatTup:(ValUUID, ValUUID)) = {
    val item1 = thisTup._1 compare thatTup._1
    if(item1 == 0) {
      thisTup._2 compare thatTup._2
    } else {
      item1
    }
  }

  def asConcrete:EdgeId = {
    val (v1, v2) = thisTup
    if(v1 < v2){
      EdgeId.newBuilder.setV1(v1.asConcrete).setV2(v2.asConcrete).build
    } else { // The names make this part confusing, but it's correct
      EdgeId.newBuilder.setV1(v2.asConcrete).setV2(v1.asConcrete).build
    }
  }
}
