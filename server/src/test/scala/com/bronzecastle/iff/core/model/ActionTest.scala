package com.bronzecastle.iff.core.model

import action.TakeAction
import org.junit._
import Assert._
import com.bronzecastle.iff.core.model.ModelException.PreconditionFailedException
import com.bronzecastle.iff.core.Relation

@Test
class ActionTest extends Environment {
  @Test
  def testTake() {
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
  }

  @Test
  def testDrop() {

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
