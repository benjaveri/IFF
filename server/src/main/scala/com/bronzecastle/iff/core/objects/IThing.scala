/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.orm.Persistent
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.Universe
import collection.mutable.{ArrayBuffer => MutableArrayBuffer}

/**
 * a thing that can be in IPlace
 *  a thing can also be a composite, containing, hiding or supporting other things
 */
trait IThing extends IObject {
  //
  // state
  //

  // borrow id from persistable
  require(this.isInstanceOf[IPersistable])
  override def ID = IPersistable.idOf(this)

  // how this thing relates to location (in, under, on, etc.)
  @Persistent var relation: Relation = Relation.In

  // where this thing is
  @Persistent(column="loc") var location = IPlace.NOWHERE

  // this thing is visible
  def isVisible = true

  // things inside or under it is visible
  def isTransparent = false

  // this thing cannot be moved
  def isFixture = false

  // actors can affect these relations (subclassed by IContainer, etc)
  def supportsRelation(rel: Relation): Boolean = false

  // weight
  def weight = 0
  def totalWeight: Int = {
    weight + listChildrenByRelation(WEIGHT_AFFECTING_RELATIONS:_*)
      .map(_.totalWeight)
      .sum
  }
  protected val WEIGHT_AFFECTING_RELATIONS = Seq(Relation.On,Relation.In,Relation.Carrying,Relation.Wearing)

  // size (assumed non-deformable, like a chest or a box)
  def bulk = 0
  def totalBulk: Int = {
    bulk + listChildrenByRelation(BULK_AFFECTING_RELATIONS:_*)
      .map(_.totalBulk)
      .sum
  }
  def maxHoldingSpace(rel: Relation) = Int.MaxValue
  def totalHoldingBulk(rel: Relation) = {
    listChildrenByRelation(rel)
      .map(_.totalBulk)
      .sum
  }
  protected val BULK_AFFECTING_RELATIONS = Seq(Relation.On,Relation.Carrying,Relation.Wearing)

  //
  // reflection
  //

  /**
   * Finds the place this thing is in
   *
   * @return the place if any, else IPlace.nowhere
   */
  def getPlace: IPlace = {
    Universe().getOption[IPersistable](location) match {
      case None => IPlace.nowhere
      case Some(inst) => inst match {
        case thing: IThing => thing.getPlace
        case place: IPlace => place
        case _ => IPlace.nowhere
      }
    }
  }

  /**
   * Evaluates whether this IThing is directly on indirectly
   *  in the given IPlace
   *
   * @param candidate the IPlace of interest
   * @return true is this is in the place, or false if not
   */
  def isInPlace(candidate: IPlace): Boolean = {
    Universe().getOption[IPersistable](location) match {
      case None => false
      case Some(inst) => inst match {
        case thing: IThing => thing.isInPlace(candidate)
        case place: IPlace => candidate.ID == place.ID
        case _ => false
      }
    }
  }

  /**
   * returns a list of all parents in this IThing's hierarchy
   */
  def listParents: Seq[IThing] = {
    val list = new MutableArrayBuffer[IThing]()

    def r2(ob: IObject) {
      ob match {
        case thing: IThing => {
          list += thing
          Universe().getOption[IPersistable](thing.location) match {
            case None => {}
            case Some(inst) => inst match {
              case thing: IThing => r2(thing)
              case _ => {}
            }
          }
        }
      }
    }
    r2(this)

    list.toSeq
  }

  /**
   * lists direct children
   */
  def listChildren: Seq[IThing] = {
    Universe()
      .listByLocation(ID).map((ob)=>ob.asInstanceOf[IThing])
      .toSeq
  }

  /**
   * lists direct children by relation
   *  (slightly less efficient, since relation is not a column in database)
   */
  def listChildrenByRelation(rels: Relation*): Seq[IThing] = {
    Universe()
      .listByLocation(ID)
      .map((ob)=>ob.asInstanceOf[IThing])
      .filter((ob)=>rels.contains(ob.relation))
      .toSeq
  }


  /**
   * lists visible direct children
   */
  def listVisibleChildren: Seq[IThing] = {
    if (isTransparent)
      Universe()
        .listByLocation(ID)
        .map((ob)=>ob.asInstanceOf[IThing])
        .filter(_.isVisible)
        .toSeq
    else
      Seq()
  }
}
