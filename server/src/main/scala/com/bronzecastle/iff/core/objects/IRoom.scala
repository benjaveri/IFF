/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

/**
 * a place with a description
 */
trait IRoom extends IPlace {
  def shortDescription(): String
  def fullDescription(): String
}
