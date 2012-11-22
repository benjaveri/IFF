package com.bronzecastle.iff.core.model

import action.{DropAction, TakeAction}
import org.junit._
import Assert._
import com.bronzecastle.iff.core.model.ModelException.PreconditionFailedException
import com.bronzecastle.iff.core.Relation

@Test
class ActionTest extends Environment {
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

  //
  // helper
  //
  def assertPreconditionFailed(body: =>Unit) {
    try {
      body // this must throw an exception, else fail
      assertTrue(false)
    } catch {
      case ex: PreconditionFailedException => assertTrue(true)
      case ex: Throwable => throw ex
    }
  }
}
