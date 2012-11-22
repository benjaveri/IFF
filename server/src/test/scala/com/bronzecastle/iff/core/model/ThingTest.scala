package com.bronzecastle.iff.core.model

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects.{IPlace, IThing, IPersistable}

@Test
class ThingTest extends Environment {
  @Test
  def weightAndBulkTest() {
    val a = new A
    val b = new B
    val c = new C
    U.persist(a,b,c)

    assertTrue(c.totalWeight==33)
    assertTrue(c.totalBulk==3)
    assertTrue(b.totalWeight==99)
    assertTrue(b.totalBulk==9)
    assertTrue(a.totalWeight==199)
    assertTrue(a.totalBulk==19)
  }
}

class A extends IPersistable with IThing {
  override def weight = 100
  override def bulk = 10
  location = IPlace.NOWHERE
}

class B extends IPersistable with IThing {
  override def weight = 66
  override def bulk = 6
  location = "A"
}

class C extends IPersistable with IThing {
  override def weight = 33
  override def bulk = 3
  location = "B"
}
