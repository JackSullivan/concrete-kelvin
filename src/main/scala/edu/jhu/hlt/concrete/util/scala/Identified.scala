package edu.jhu.hlt.concrete.util.scala

/**
 * @author John Sullivan
 */
trait Identified[T] {
  def id:T
  final override def equals(that:Any):Boolean = that.isInstanceOf[this.type] && this.id == that.asInstanceOf[this.type].id
}
