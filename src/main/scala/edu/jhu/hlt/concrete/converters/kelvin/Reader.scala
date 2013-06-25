package edu.jhu.hlt.concrete.converters.kelvin

import java.util.logging.Logger
import java.io.{DataOutputStream, FileOutputStream}
import scala.io.Source
import edu.jhu.hlt.concrete.Concrete.{Edge, Vertex}

/**
 * @author John Sullivan
 */
object Reader {
  def apply(filename:String):(Iterable[Vertex], Iterable[Edge]) = {
    val lines:Seq[KelvinLine] = Source.fromFile(filename).getLines.flatMap(KelvinLine.apply).toSeq
    val edges:Iterable[Edge] = lines.collect{case l:Edgeable with KelvinLine => l}.groupBy(_.id).mapValues{lineSeq =>
      lineSeq.foldLeft[Edge.Builder](Edge.newBuilder){ (a, b) =>
        a.mergeFrom(b.toBuilder.buildPartial)
      }.build
    }.values
    val vertices:Iterable[Vertex] = lines.collect{case l:Vertexable with KelvinLine => l}.groupBy(_.id).mapValues{lineSeq =>
      lineSeq.foldLeft[Vertex.Builder](Vertex.newBuilder){ (a, b) =>
        a.mergeFrom(b.toBuilder.buildPartial)
      }.build
    }.values
    vertices -> edges
  }

  def main(args:Array[String]) {
    val log = Logger.getLogger(Reader.getClass.getName)
    val filename:String = System.getProperty("KelvinFile")
    log.info("Parsing " + filename)
    val (vertices, edges) = Reader(filename)
    log.info(filename + " parsed to vertices and edges")
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
