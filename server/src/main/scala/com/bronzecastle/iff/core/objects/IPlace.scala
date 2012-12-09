/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.model.Universe
import com.bronzecastle.iff.core.Relation

/**
 * a place than can "hold" things
 */
trait IPlace extends IObject with IVocalThing {
  //
  // state
  //

  // borrow id from persistable
  require(this.isInstanceOf[IPersistable])
  override def ID = IPersistable.idOf(this)

  // exits
  def exits: PartialFunction[Relation,String] = Map.empty

  //
  // methods
  //
  /**
   * lists direct children
   */
  def listChildren: Seq[IThing] = {
    Universe().listByLocation(ID).map((ob)=>ob.asInstanceOf[IThing]).toSeq
  }

  /**
   * lists visible direct children
   */
  def listVisibleChildren: Seq[IThing] = {
    listChildren
      .filter(_.isVisible)
  }
}

object IPlace {
  val NOWHERE = "$NOWHERE" // we should define this place in the universe so we dont have to special case for it
  val nowhere = new IPersistable with IPlace { id = NOWHERE }
}
