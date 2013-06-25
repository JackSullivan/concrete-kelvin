package edu.jhu.hlt.concrete.util.scala

import java.util.{UUID => JUUID}
import edu.jhu.hlt.concrete.Concrete.{UUID => ConUUID}

/**
 * @author John Sullivan
 */
case class ValUUID(high:Long, low:Long) extends Ordered[ValUUID] {
  private val juuid = new JUUID(high, low)
  def this(juuid:JUUID) = this(juuid.getMostSignificantBits, juuid.getLeastSignificantBits)
  def this(cuuid:ConUUID) = this(cuuid.getHigh, cuuid.getLow)

  def asConcrete:ConUUID = ConUUID.newBuilder.setHigh(high).setLow(low).build
  def asJava:JUUID = juuid

  def compare(that:ValUUID) = juuid compareTo that.juuid
  override def toString = juuid.toString

  def ->(that:ValUUID):(ValUUID, ValUUID) = if(this < that) (this, that) else (that, this)
}

object ValUUID {
  def random:ValUUID = new ValUUID(JUUID.randomUUID)
}
