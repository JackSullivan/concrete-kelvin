package edu.jhu.hlt.concrete.converters.kelvin

import java.io.File
import java.util.logging.Logger
import java.io.FileOutputStream
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

  def processFile(log: Logger,f: File)
  {
    log.info("Parsing " + f.getName)
    val kg:KnowledgeGraph = Reader(f.getPath)
    log.info(f.getName + " parsed to knowledge graph")    
    val out= new FileOutputStream(f.getPath + ".pb")
    kg.writeTo(out)
    out.flush
    out.close
    log.info("Wrote to " + f.getName);
  }
 

  def main(args:Array[String])
  {
    val log = Logger.getLogger(Reader.getClass.getName)
    val filename:String = System.getProperty("KelvinFile")
    val file= new File(filename)
    if (file.isDirectory){
	val files = new File(filename).listFiles.filter(_.isFile)
    	for(f <- files) processFile(log,f)
    }else processFile(log,file)
  }

}
