/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff
package core

import model.Universe
import objects._
import org.junit._
import Assert._
import objects.IPersistable._

@Test
class LocationTest {
  var U: Universe = null

  @Before
  def startup() {
    U = Universe.startup("mem:test")

    // this seems weird - wonder if we can import them automagically -
    //  probably not so easy, consider a saved game vs new game.
    U.persist(new Cell)
    U.persist(new Passage)
    U.persist(new Sand)
    U.persist(new Cache)
    U.persist(new Key)
    U.persist(new Rock)
    U.persist(new Me)
  }

  @After
  def shutdown() {
    U.shutdown()
  }

  @Test
  def test() {
    val key = U.getInstance(classOf[Key])
    val cell = U.getInstance(classOf[Cell])
    assertTrue(key.isInRoom(cell))
    //assertTrue(key.isVisible(cell))
    val rock = U.getInstance(classOf[Rock])
    assertFalse(rock.isInRoom(cell))
    //assertFalse(rock.isVisible(cell))
  }


}

//
// a simple world
//
class Cell extends IPersistable with IRoom {
  def shortDescription() = "Cell"
  def fullDescription() = "An uncomfortable small cell somewhere surely underground."
}

class Passage extends IPersistable with IRoom {
  def shortDescription() = "Passage"
  def fullDescription() = "A rocky passage hewn out of bedrock."
}

class Sand extends IPersistable with IThing {
  def shortDescription() = "sand"
  def fullDescription() = "A heap of sand. Not particularly different from most sand you've seen."
  location = indexOf(classOf[Cell])
}

class Cache extends IPersistable with IThing {
  def shortDescription() = "small/cache"
  def fullDescription() = "A small cache."
  relation = Relation.Under
  location = indexOf(classOf[Sand])
}

class Key extends IPersistable with IThing {
  def shortDescription() = "small/key"
  def fullDescription() = "A small key."
  location = indexOf(classOf[Cache])
}

class Rock extends IPersistable with IThing {
  def shortDescription() = "rock"
  def fullDescription() = "Just another rock."
  location = indexOf(classOf[Passage])
}

class Me extends IPersistable with IActor {
  location = indexOf(classOf[Cell])
}
