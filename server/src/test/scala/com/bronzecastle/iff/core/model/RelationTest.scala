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
class RelationTest extends WorldEnvironment {
  @Test
  def testIsInPlace() {
    U.logInventory()
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

