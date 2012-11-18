/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.orm.Persistent

/**
 * something to be remembered
 */
trait IPersistable extends IObject {
  // required instance name
  @Persistent(column = "idx")
  var id = getClass.getSimpleName

  // generation number
  @Persistent(column = "gen")
  var gen = -1L

  // persistence triggers
  def triggerAboutToPersist() {}
  def triggerJustLoaded() {}
}

object IPersistable {
  def idOf(ob: Any): String = ob match {
    case p: IPersistable => p.id
    case x => idOf(x.asInstanceOf[IPersistable])
  }
}