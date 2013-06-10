package edu.jhu.hlt.concrete.converters.kelvin

import edu.jhu.hlt.concrete.Concrete.{EdgeId, Edge}
import edu.jhu.hlt.concrete.converters.kelvin.CommunicationTraits.{FinalMessage, Edgeable, MessageableSeq2Message}

/**
 * @author John Sullivan
 */
case class EdgeMention(_id:EdgeId, lines:Seq[Edgeable]) extends Edgeable with FinalMessage[Edge, EdgeId] {
  val relations:Seq[VertexMentionRelation] = lines collect {case l:VertexMentionRelation => l}

  def toBuilder:Edge.Builder = {
    val edg = Edge.newBuilder.setEdgeId(_id)
    if(relations.nonEmpty) edg.mergeFrom(relations)
    edg
  }

  def toMessage:Edge = toBuilder.build

  def id:EdgeId = _id
}

object EdgeMention {
  def apply(lines:Seq[Edgeable]):Iterable[EdgeMention] = lines.groupBy(_.id) map {case (id, l) => EdgeMention(id, l)}
}
