/* 
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved. 
 * This software is released under the 2-clause BSD license. See LICENSE in the project root directory. 
 */

package edu.jhu.hlt.concrete.util.scala

/**
 * @author John Sullivan
 */
trait Identified[T] {
  def id:T
  final override def equals(that:Any):Boolean = that.isInstanceOf[this.type] && this.id == that.asInstanceOf[this.type].id
}
