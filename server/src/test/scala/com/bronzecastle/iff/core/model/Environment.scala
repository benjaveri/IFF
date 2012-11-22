/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.model

import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.Relation
import org.junit.{After, Before}

/**
 * a simple world we use in many different tests
 */
class Environment
{
  var U: Universe = null

  @Before
  def startup() {
    U = Universe.startup("mem:test")

    // this seems weird - wonder if we can import them automagically -
    //  probably not so easy, consider a saved game vs new game.
    Rock.reset()
    Goblin.reset()
    U.persist(
      new Cell,
      new Sand,new Cache,new Key,
      new Shield,
      new Me,new Ghost,
      new Passage,
      new Rock,new Rock,
      new Goblin,new Goblin
    )
  }

  @After
  def shutdown() {
    U.shutdown()
    U = null
  }
}

//
// a simple world
//
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

class Cache extends IPersistable with IThing {
  def shortDescription() = "small/cache"
  def fullDescription() = "A small cache."
  relation = Relation.Under
  location = "Sand"
}

class Key extends IPersistable with IThing {
  def shortDescription() = "small,tiny/key"
  def fullDescription() = "A small key."
  location = "Cache"
}

class Passage extends IPersistable with IRoom {
  def shortDescription() = "Passage"
  def fullDescription() = "A rocky passage hewn out of bedrock."
}

class Rock extends IPersistable with IThing {
  id = "Rock#"+Rock.nextSerialNumber()
  def shortDescription() = "rock"+id.last
  def fullDescription() = "Just another rock."
  location = "Passage"
}
object Rock extends SerialNumber

class Goblin extends IPersistable with IActor {
  id = "Goblin#"+Goblin.nextSerialNumber()
  location = "Passage"
}
object Goblin extends SerialNumber

class SerialNumber {
  protected var serial = 0
  def reset() { serial = 0 }
  def nextSerialNumber() = { serial+=1; serial }
}