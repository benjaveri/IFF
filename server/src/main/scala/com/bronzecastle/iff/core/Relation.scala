/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core

import orm.{ RichInputStream, RichOutputStream}
import collection.mutable.{HashMap => MutableHashMap}

/**
 * relationships
 */
class Relation(val ord: Int)

object Relation {
  // compass
  class Direction(o: Int) extends Relation(o)
  case object North extends Direction(0)
  case object NorthEast extends Direction(1)
  case object East extends Direction(2)
  case object SouthEast extends Direction(3)
  case object South extends Direction(4)
  case object SouthWest extends Direction(5)
  case object West extends Direction(6)
  case object NorthWest extends Direction(7)
  case object Up extends Direction(8)
  case object Down extends Direction(9)
  case object In extends Direction(10)
  case object Out extends Direction(11)
  case object Enter extends Direction(12)
  case object Leave extends Direction(13)

  // inter-object
  case object On extends Relation(14)
  case object Under extends Relation(15)

  // actor related
  case object Carrying extends Relation(100)
  case object Wearing extends Relation(101)

  // serialization
  def write(ros: RichOutputStream,rel: Relation) {
    ros.writeInt(rel.ord)
  }
  def read(ris: RichInputStream): Relation = {
    ORD_TO_REL(ris.readInt())
  }

  // unfortunately we need to list all serializable relations
  private val ORD_TO_REL: Map[Int,Relation] = List(
    North,NorthEast,East,SouthEast,South,SouthWest,West,NorthWest,
    Up,Down,
    In,Out,
    Enter,Leave,
    On,Under,
    Carrying,Wearing
  ).map((ob) => {ob.ord -> ob}).toMap
}

object Direction {
  val North = Relation.North
  val NorthEast = Relation.NorthEast
  val East = Relation.East
  val SouthEast = Relation.SouthEast
  val South = Relation.South
  val SouthWest = Relation.SouthWest
  val West = Relation.West
  val NorthWest = Relation.NorthWest
  val Up = Relation.Up
  val Down = Relation.Down
  val In = Relation.In
  val Out = Relation.Out
  val Enter = Relation.Enter
  val Leave = Relation.Leave
}