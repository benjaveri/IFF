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
 * a place characters can visit
 */
trait ILocation extends IObject {
  @Persistent(column="loc") var loc = ILocation.NOWHERE

  def shortDesc(): String
  def fullDesc(): String
}

object ILocation {
  val NOWHERE = "$NOWHERE"
}