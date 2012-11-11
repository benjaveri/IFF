package com.bronzecastle.iff.core.model

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects.{IPersistable, IObject}
import java.io.{ObjectInputStream, ByteArrayInputStream, ObjectOutputStream, ByteArrayOutputStream}
import org.apache.log4j.Logger

@Test
class ORMTest {
  val LOG = Logger.getLogger(getClass)

  @Test
  def testPersistence() {
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
