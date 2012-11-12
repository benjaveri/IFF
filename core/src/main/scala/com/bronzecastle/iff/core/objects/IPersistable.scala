/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.orm.Persistent

trait IPersistable {
  // required instance name
  @Persistent(column = "idx")
  var index = getClass.getSimpleName

  // generation number
  @Persistent(column = "gen")
  var gen = -1L

  // persistence triggers
  def triggerAboutToPersist() {}
  def triggerJustLoaded() {}
}
