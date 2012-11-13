/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core

/**
 * relationships
 */
trait Relation

object Relation extends Enumeration {
  // compass
  trait Direction extends Relation
  case object North extends Direction
  case object NorthEast extends Direction
  case object East extends Direction
  case object SouthEast extends Direction
  case object South extends Direction
  case object SouthWest extends Direction
  case object West extends Direction
  case object NorthWest extends Direction
  case object Up extends Direction
  case object Down extends Direction
  case object In extends Direction
  case object Out extends Direction
  case object Enter extends Direction
  case object Leave extends Direction

  // inter-object
  case object On extends Relation
  case object Under extends Relation

  // serialization
  def toString(s: Relation): String = {
    s.toString
  }
  def fromString(s: String): Relation = {
    val clazz = Class.forName("com.bronzecastle.iff.core.Relation$"+s+"$")
    clazz.newInstance().asInstanceOf[Relation]
  }
}
