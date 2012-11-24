/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core
package model

import org.junit._
import Assert._
import objects._

@Test
class RelationTest
  extends Environment(
    new Cell,new Passage,
    new Me, new Ghost,
    new Sand,new Cache,new Key,
    Rock(1),Rock(2),
    Goblin(1),Goblin(2)
  )
{
  @Test
  def testIsInPlace() {
    val key = U.get[Key]
    val cell = U.get[Cell]
    assertTrue(key.isInPlace(cell))
    val rock = U.get[Rock]("Rock#1")
    assertFalse(rock.isInPlace(cell))
  }

  @Test
  def testListParents() {
    val key = U.get[Key]
    val list = key.listParents
    assertTrue(list.size == 3)
    assertTrue(list(0).location == "Cache")
    assertTrue(list(1).location == "Sand")
    assertTrue(list(2).location == "Cell")
  }

  @Test
  def testListChildren() {
    val cell = U.get[Cell]
    val inCell = cell.listChildren.map(_.ID).toArray
    assertTrue(inCell.diff(Seq("Me","Ghost","Sand","Shield")).isEmpty)
    val sand = U.get[Sand]
    val inSand = sand.listChildren.map(_.ID).toArray
    assertTrue(inSand.diff(Seq("Cache")).isEmpty)
  }

  @Test
  def testListVisibleChildren() {
    val cell = U.get[Cell]
    val inCell = cell.listVisibleChildren.map(_.ID).toArray
    assertTrue(inCell.diff(Seq("Me","Sand","Shield")).isEmpty)
    val sand = U.get[Sand]
    val inSand = sand.listVisibleChildren.map(_.ID).toArray
    assertTrue(inSand.isEmpty)
  }
}


class Cell extends IPersistable with IRoom {
  def shortDescription() = "Cell"
  def fullDescription() = "An uncomfortable small cell somewhere underground."
}

class Me extends IPersistable with IActor {
  override def maxCarrySpace = 5
  override def maxCarryWeight = 5
  location = "Cell"
}

class Shield extends IPersistable with IThing {
  def shortDescription() = "battered,copper/shield"
  def fullDescription() = "A small battered shield made of copper."
  override def bulk = 1
  override def weight = 1
  location = "Cell"
}

class Sand extends IPersistable with IFixture {
  def shortDescription() = "sand"
  def fullDescription() = "A heap of sand. Not particularly different from most sand you've seen."
  location = "Cell"
}

class Ghost extends IPersistable with IActor {
  override def isVisible = false
  location = "Cell"
}

class Cache extends IPersistable with IContainer {
  def shortDescription() = "small/cache"
  def fullDescription() = "A small cache."
  relation = Relation.Under
  location = "Sand"
}

class Key extends IPersistable with IThing {
  def shortDescription() = "small,tiny/key"
  def fullDescription() = "A small key."
  location = "Cache"
  relation = Relation.In
}

class Passage extends IPersistable with IRoom {
  def shortDescription() = "Passage"
  def fullDescription() = "A rocky passage hewn out of bedrock."
}

class Rock extends IPersistable with IThing {
  def shortDescription() = "rock"+ID.last
  def fullDescription() = "Just another rock."
  location = "Passage"
}
object Rock{
  def apply(sn: Int) = {
    val g = new Rock
    g.id += "#"+sn
    g
  }
}

class Goblin extends IPersistable with IActor {
  location = "Passage"
}
object Goblin {
  def apply(sn: Int) = {
    val g = new Goblin
    g.id += "#"+sn
    g
  }
}

