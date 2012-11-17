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

@Test
class RelationTest extends Environment {
  @Test
  def testIsInRoom() {
    val key = U.get[Key]
    val cell = U.get[Cell]
    assertTrue(key.isInRoom(cell))
    val rock = U.get[Rock]("Rock#1")
    assertFalse(rock.isInRoom(cell))
  }

  @Test
  def testGetParents() {
    val key = U.get[Key]
    val list = key.getParents()
    assertTrue(list.size == 3)
    assertTrue(list(0).location == "Cache")
    assertTrue(list(1).location == "Sand")
    assertTrue(list(2).location == "Cell")
  }
}

