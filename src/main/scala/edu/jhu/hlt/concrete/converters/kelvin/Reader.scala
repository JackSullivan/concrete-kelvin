package edu.jhu.hlt.concrete.converters.kelvin

import scala.io.Source
import edu.jhu.hlt.concrete.Concrete.KnowledgeGraph
import edu.jhu.hlt.concrete.converters.kelvin.CommunicationTraits.{Vertexable, Edgeable}
import edu.jhu.hlt.concrete.converters.kelvin.AttributeConversions._
import scala.collection.JavaConverters._

/**
 * @author John Sullivan
 */
object Reader {
  def apply(filename:String):KnowledgeGraph = {
    val lines:Seq[KelvinLine] = (Source.fromFile(filename).getLines map {KelvinLine}).flatten.toSeq
    val edgeLines:Iterable[EdgeMention] = EdgeMention(lines.collect{case l:Edgeable => l})
    val vertexLines:Iterable[VertexMention] = VertexMention(lines.collect{case l:Vertexable => l})
    KnowledgeGraph.newBuilder
      .setUuid(genUUID)
      .addAllVertex((vertexLines map {_.toMessage}).asJava)
      .addAllEdge((edgeLines map {_.toMessage}).asJava)
      .build
  }
}
