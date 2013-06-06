package edu.jhu.hlt.concrete.converters.kelvin

import scala.collection.JavaConverters._
import edu.jhu.hlt.concrete.Concrete.{Vertex, UUID => ConUUID}
import edu.jhu.hlt.concrete.Concrete.Vertex.RelationSet

/**
 * @author John Sullivan
 */
case class VertexMention(uuid:ConUUID, lines:Seq[KelvinLine]) {
  val types:Seq[MentionType] = lines collect {case l:MentionType => l}
  val texts:Seq[MentionText] = lines collect {case l:MentionText => l}
  val relations:Seq[MentionRelation] = lines collect {case l:MentionRelation => l}

  def toVertex:Vertex = Vertex.newBuilder
    .setUuid(uuid)
    .addAllKind((types map {_.toVertexKindAttribute}).asJava)
    .addAllName((texts map {_.toStringAttribute}).asJava)
    .addRelationSetList {
    RelationSet.newBuilder
      .setUuid(KelvinLine.genUUID)
      .addAllRelations((relations map {_.toRelation}).asJava)
      .build
    }.build

}

object VertexMention {
  def apply(lines:Seq[KelvinLine]):Iterable[VertexMention] = lines.groupBy(_.vertexUUID) map {case (uuid, l) => new VertexMention(uuid, l)}
}