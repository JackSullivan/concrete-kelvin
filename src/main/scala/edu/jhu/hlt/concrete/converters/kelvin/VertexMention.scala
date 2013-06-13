package edu.jhu.hlt.concrete.converters.kelvin

import edu.jhu.hlt.concrete.Concrete.{Vertex, UUID => ConUUID}
import edu.jhu.hlt.concrete.converters.kelvin.CommunicationTraits.{FinalMessage, Vertexable, MessageableSeq2Message}

/**
 * @author John Sullivan
 */
case class VertexMention(uuid:ConUUID, lines:Seq[Vertexable]) extends Vertexable with FinalMessage[Vertex, ConUUID] {
  val types:Seq[MentionType] = lines collect {case l:MentionType => l}
  val texts:Seq[MentionText] = lines.collect{case l:MentionText => l}.groupBy(_.mentionText).values.map{_.head}.toSeq // to remove duplicate names
  val relations:Seq[ValueMentionRelation] = lines collect {case l:ValueMentionRelation => l}

  def toBuilder:Vertex.Builder = { //todo set DataSetId
    val vert = Vertex.newBuilder.setUuid(uuid)
    if(types.nonEmpty) vert.mergeFrom(types)
    if(texts.nonEmpty) vert.mergeFrom(texts)
    if(relations.nonEmpty) vert.mergeFrom(relations)
    vert
  }

  def toMessage:Vertex = toBuilder.build

  def id:ConUUID = uuid
}

object VertexMention {
  def apply(lines:Seq[Vertexable]):Iterable[VertexMention] = lines.groupBy(_.id) map {case (uuid, l) => new VertexMention(uuid, l)}
}