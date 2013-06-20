package edu.jhu.hlt.concrete.converters.kelvin

import java.util.logging.Logger
import java.io.{DataOutputStream, FileOutputStream}
import scala.io.Source
import edu.jhu.hlt.concrete.Concrete.{Edge, Vertex}
import edu.jhu.hlt.concrete.converters.kelvin.CommunicationTraits.{Vertexable, Edgeable}

/**
 * @author John Sullivan
 */
object Reader {
  def apply(filename:String):(Iterable[Vertex], Iterable[Edge]) = {
    val lines:Seq[KelvinLine] = (Source.fromFile(filename).getLines map {KelvinLine}).flatten.toSeq
    val edgeLines:Iterable[EdgeMention] = EdgeMention(lines.collect{case l:Edgeable => l})
    val vertexLines:Iterable[VertexMention] = VertexMention(lines.collect{case l:Vertexable => l})
    (vertexLines.map{_.toMessage}, edgeLines.map{_.toMessage})
  }

  def main(args:Array[String]) {
    val log = Logger.getLogger(Reader.getClass.getName)
    val filename:String = System.getProperty("KelvinFile")
    log.info("Parsing " + filename)
    val (vertices, edges) = Reader(filename)
    log.info(filename + " parsed to knowledge graph")
    val wrt = new DataOutputStream(new FileOutputStream(filename + ".pb"))
    wrt.writeInt(vertices.size)
    vertices.foreach(_.writeDelimitedTo(wrt))
    wrt.writeInt(edges.size)
    edges.foreach(_.writeDelimitedTo(wrt))
    wrt.flush
    wrt.close
    log.info("Wrote to " + filename + ".pb")
  }
}
