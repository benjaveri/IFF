/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.model

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects.{IPersistable, IObject}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.apache.log4j.Logger

@Test
class ORMTest {
  val LOG = Logger.getLogger(getClass)

  @Test
  def testSerialization() {
    val a = new ObjectA
    a.blinkRate = 10
    a.isLocked = false
    a.nameOfKey = "x"
    a.a = 123
    a.b = 456

    val bos = new ByteArrayOutputStream()
    a.serialize(bos)
    bos.close()

    val bis = new ByteArrayInputStream(bos.toByteArray)
    val a2 = IPersistable.getInstance(bis)
    bis.close()

    assertTrue(a2.isInstanceOf[ObjectA])
    val o = a2.asInstanceOf[ObjectA]
    assertTrue(o.blinkRate==10)
    assertTrue(o.isLocked==false)
    assertTrue(o.nameOfKey=="x")
    assertTrue(o.a==123)
    assertTrue(o.b==0) // b was not marked persistent
  }

  @Test
  def testPersistence() {
    val db = new Database("mem:test")
    ORM.createTables(db)

    val a = new ObjectA
    a.a = 123
    assertTrue(ORM.persist(db,a))
    assertTrue(a.gen == 0)
    a.a = 456
    assertTrue(ORM.persist(db,a))
    assertTrue(a.gen == 1)

    val b = ORM.getInstance(db,a.index()).get.asInstanceOf[ObjectA]
    assertTrue(b.gen == 1)
    assertTrue(b.a == 456)

    db.shutdown()
  }

  @Test
  def testUpdateContention() {
    val db = new Database("mem:test")
    ORM.createTables(db)

    val a = new ObjectA
    a.a = 123
    assertTrue(ORM.persist(db,a))

    val b = ORM.getInstance(db,a.index()).get.asInstanceOf[ObjectA]
    b.a = 456
    assertTrue(ORM.persist(db,b))

    a.a = 999
    assertFalse(ORM.persist(db,a))
    assertTrue(ORM.refresh(db,a))
    assertTrue(a.a == 456)
    a.a = 999
    assertTrue(ORM.persist(db,a))

    db.shutdown()
  }
}

trait IBlinkable extends IObject {
  @Persistent var blinkRate: Int = 50
}
trait IOpenable extends IObject {
  @Persistent var isLocked = true
  @Persistent var nameOfKey = "gold"
}
class ObjectA extends IPersistable with IBlinkable with IOpenable {
  @Persistent var a = 0L
  var b = 0L
}
