/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core

import model.Universe
import objects.{IPersistable, IActor, ILocation}
import org.junit._
import Assert._

@Test
class LocationTest {

  @Test
  def test() {
    val U = Universe.startup("mem:test")
    U.register(new RoomA,new RoomB,new Observer)
    U.shutdown()
  }


}

class RoomA extends IPersistable with ILocation {
  def shortDesc() = "Room A"
  def fullDesc() = "Room A"
}
class RoomB extends IPersistable with ILocation {
  def shortDesc() = "Room B"
  def fullDesc() = "Room B"
}

class Observer extends IPersistable with IActor {
}
