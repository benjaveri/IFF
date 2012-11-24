package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.Relation

/**
 * a thing you can put other things on
 */
trait ISurface extends IThing {
  override def supportsRelation(rel: Relation) = rel match {
    case Relation.On => true
    case _ => super.supportsRelation(rel)
  }

  // how much it can hold (by default it holds as much as it is big)
  def capacityOn = bulk
  override def maxHoldingSpace(rel: Relation) = rel match {
    case Relation.On => capacityOn
    case _ => super.maxHoldingSpace(rel)
  }
}
