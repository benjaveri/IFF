/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects.{IPersistable, IThing}
import org.apache.log4j.Logger

@Test
class ORMTest {
  @Test
  def testPersistence() {
    val db = new Database("mem:test")
    Registry.createTables(db)

    val a = new ThingA
    a.a = 123
    assertTrue(SavePersistable(db,a))
    assertTrue(a.gen == 0)
    a.a = 456
    a.b = 456
    assertTrue(SavePersistable(db,a))
    assertTrue(a.gen == 1)

    val b = LoadPersistable(db,a.index).get.asInstanceOf[ThingA]
    assertTrue(b.gen == 1)
    assertTrue(b.a == 456)
    assertTrue(b.b == 0) // b is not marked as persistent

    db.shutdown()
  }

  @Test
  def testUpdateContention() {
    val db = new Database("mem:test")
    Registry.createTables(db)

    val a = new ThingA
    a.a = 123
    assertTrue(SavePersistable(db,a))

    val b = LoadPersistable(db,a.index).get.asInstanceOf[ThingA]
    b.a = 456
    assertTrue(SavePersistable(db,b))

    a.a = 999
    assertFalse(SavePersistable(db,a))
    assertTrue(LoadPersistable(db,a.index,a).isDefined)
    assertTrue(a.a == 456)
    a.a = 999
    assertTrue(SavePersistable(db,a))

    db.shutdown()
  }
}

trait ITrait1 {
  @Persistent var intValue: Int = 50
  @Persistent var longValue: Long = -50L
}

trait ITrait2 {
  @Persistent var boolValue = true
  @Persistent var stringValue = "gold"
}

class ThingA extends IPersistable with ITrait1 with ITrait2 {
  @Persistent var a = 0L
  var b = 0L
}