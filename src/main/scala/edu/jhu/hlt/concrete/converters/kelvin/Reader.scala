/* 
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved. 
 * This software is released under the 2-clause BSD license. See LICENSE in the project root directory. 
 */

package edu.jhu.hlt.concrete.converters.kelvin

import java.util.logging.Logger
import scala.io.Source
import edu.jhu.hlt.concrete.Graph.{Edge, Vertex}
import edu.jhu.hlt.concrete.io.ProtocolBufferWriter

/**
 * @author John Sullivan, Jay DeYoung
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
    val (edgeFile,vertexFile) = (filename + ".edg.pb", filename+".ver.pb")
    log.info("Parsing " + filename)
    val (vertices, edges) = Reader(filename)
    log.info(filename + " parsed to vertices and edges")
    val vertexWriter = new ProtocolBufferWriter(vertexFile)
    val edgeWriter = new ProtocolBufferWriter(edgeFile)
    for(vertex <- vertices) {
      vertexWriter.write(vertex)
    }
    vertexWriter.close()
    log.info("wrote vertices to " + vertexFile)
    for(edge <- edges) {
      edgeWriter.write(edge)
    }
    edgeWriter.close()
    log.info("wrote edges to " + edgeFile)
  }
}
