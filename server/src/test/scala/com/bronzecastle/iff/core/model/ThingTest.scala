package com.bronzecastle.iff.core.model

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.Relation
import ThingTest._

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
    println(b.totalWeight)
    val x = b.listChildrenByRelation(Relation.On,Relation.In,Relation.Carrying).map(_.ID).toArray
    assertTrue(b.totalWeight==99)
    assertTrue(b.totalBulk==6)
    assertTrue(a.totalWeight==199)
    assertTrue(a.totalBulk==16)
  }
}

object ThingTest {
  class A extends IPersistable with IDeformableContainer {
    override def weight = 100
    override def bulk = 10
    location = IPlace.NOWHERE
  }

  class B extends IPersistable with IContainer {
    override def weight = 66
    override def bulk = 6
    location = "A"
    relation = Relation.In
  }

  class C extends IPersistable with IThing {
    override def weight = 33
    override def bulk = 3
    location = "B"
    relation = Relation.In
  }

  class Bag extends IPersistable with IDeformableContainer
}