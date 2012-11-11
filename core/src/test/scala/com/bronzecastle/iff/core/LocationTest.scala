package com.bronzecastle.iff.core

import model.Universe
import objects.{IActor, ILocation}
import org.junit._
import Assert._

@Test
class LocationTest {
  class RoomA extends ILocation {
    def shortDesc() = "Room A"
    def fullDesc() = "Room A"
  }
  class RoomB extends ILocation {
    def shortDesc() = "Room B"
    def fullDesc() = "Room B"
  }

  class Observer extends IActor {

  }

  @Test
  def test() {
    val U = new Universe("mem:test")
    U.create()
    U.register(new RoomA,new RoomB,new Observer)

    U.destroy()
  }


}
