package com.bronzecastle.iff.core.model.action

import org.junit.Test
import org.junit.Assert._
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.Environment
import com.bronzecastle.iff.core.objects._
import TakeDropActionTest._

@Test
class TakeDropActionTest
  extends Environment (
    new Me,new Cell,new Sand,new Key,new Shield
  )
{
  @Test
  def testTakeDrop1() {
    // can take shield since its visible
    val me = U.get[Me]
    val shield = U.get[Shield]
    assertTrue(shield.location=="Cell")
    assertTrue(me.totalCarrySpace == 0)
    assertTrue(me.totalCarryWeight == 0)
    me.act(TakeAction,shield)
    assertTrue(shield.location=="Me" && shield.relation==Relation.Carrying)
    assertTrue(me.totalCarrySpace == 1)
    assertTrue(me.totalCarryWeight == 1)

    // cannot take key since its not visible
    assertPreconditionFailed(me.act(TakeAction,U.get[Key]))

    // cannot take sand since it is a fixture
    assertPreconditionFailed(me.act(TakeAction,U.get[Sand]))

    // drop shield
    me.act(DropAction,shield)
    assertTrue(shield.location=="Cell")
    assertTrue(me.totalCarrySpace == 0)
    assertTrue(me.totalCarryWeight == 0)
  }

  @Test
  def testTakeDrop2() {
    // get on sand (force, since we're not testing actor travel here)
    val me = U.get[Me]
    me.location = "Sand"
    me.relation = Relation.On
    assertTrue(U.persist(me))

    // take shield
    val shield = U.get[Shield]
    me.act(TakeAction,shield)
    assertTrue(shield.location=="Me" && shield.relation==Relation.Carrying)

    // drop shield
    me.act(DropAction,shield)
    assertTrue(shield.location=="Sand")
    assertTrue(shield.relation==Relation.On)

    // get off sand so other tests can assume default environment
    me.location = "Cell"
    me.relation = Relation.In
    assertTrue(U.persist(me))
  }
}

object TakeDropActionTest {
  class Cell extends IPersistable with IRoom {
    def shortDescription() = "Cell"
    def fullDescription() = "An uncomfortable small cell somewhere underground."
  }

  class Me extends IPersistable with IActor {
    override def maxCarrySpace = 5
    override def maxCarryWeight = 5
    location = "Cell"
  }

  class Shield extends IPersistable with IThing {
    def shortDescription() = "battered,copper/shield"
    def fullDescription() = "A small battered shield made of copper."
    override def bulk = 1
    override def weight = 1
    location = "Cell"
  }

  class Sand extends IPersistable with IFixture {
    def shortDescription() = "sand"
    def fullDescription() = "A heap of sand. Not particularly different from most sand you've seen."
    location = "Cell"
  }

  class Key extends IPersistable with IThing {
    def shortDescription() = "small,tiny/key"
    def fullDescription() = "A small key."
    location = "Cache"
    relation = Relation.In
  }
}