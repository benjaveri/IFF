/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.model.Universe


/**
 * a place than can "hold" things
 */
trait IPlace extends IObject {
  /**
   * lists direct children
   */
  def listChildren: Seq[IThing] = {
    Universe().listByLocation(IPersistable.idOf(this)).map((ob)=>ob.asInstanceOf[IThing]).toSeq
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
}
