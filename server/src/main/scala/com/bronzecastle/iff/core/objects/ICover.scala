package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.Relation

/**
 * a thing you can put other things under
 */
trait ICover extends IThing {
  override def supportsRelation(rel: Relation) = rel match {
    case Relation.Under => true
    case _ => super.supportsRelation(rel)
  }

  // how much it can hold (by default it holds as much as it is big)
  def capacityUnder = bulk
  override def maxHoldingSpace(rel: Relation) = rel match {
    case Relation.Under => capacityUnder
    case _ => super.maxHoldingSpace(rel)
  }
}
