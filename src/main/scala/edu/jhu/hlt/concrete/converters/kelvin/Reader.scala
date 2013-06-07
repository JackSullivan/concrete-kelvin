package edu.jhu.hlt.concrete.converters.kelvin

import scala.io.Source
import edu.jhu.hlt.concrete.Concrete.Vertex

/**
 * @author John Sullivan
 */
object Reader {
  def apply(fileName:String):Iterable[Vertex] = VertexMention((Source.fromFile(fileName).getLines map {KelvinLine}).flatten.toSeq) map {_.toVertex}
}
