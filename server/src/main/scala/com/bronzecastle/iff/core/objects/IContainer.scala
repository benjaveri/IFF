package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.Relation

/**
 * a thing that you can put other things in
 */
trait IContainer extends IThing {
  override def supportsRelation(rel: Relation) = rel match {
    case Relation.In => true
    case _ => super.supportsRelation(rel)
  }

  // how much it can hold (by default it holds as much as it is big)
  def capacityInside = bulk
  override def maxHoldingSpace(rel: Relation) = rel match {
    case Relation.In => capacityInside
    case _ => super.maxHoldingSpace(rel)
  }
}

/**
 * a container whose bulk depends on its contents, like a bag
 */
trait IDeformableContainer extends IContainer {
  override def totalBulk: Int = super.totalBulk + listChildrenByRelation(Relation.In)
    .map(_.totalBulk)
    .sum
}
