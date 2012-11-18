package com.bronzecastle.iff.core.model

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects.TakeAction
import com.bronzecastle.iff.core.model.ModelException.UnableToPerformActionException
import com.bronzecastle.iff.core.Relation

@Test
class ActionTest extends Environment {
  @Test
  def testTake() {
    // can take sand since its visible
    val me = U.get[Me]
    val sand = U.get[Sand]
    assertTrue(sand.location=="Cell")
    me.act(TakeAction,sand)
    assertTrue(sand.location=="Me" && sand.relation==Relation.Carrying)

    // cannot take key since precondition fails (key not visible)
    assertUnableToPerform(me.act(TakeAction,U.get[Key]))
  }

  @Test
  def testDrop() {

  }



  def assertUnableToPerform(f: =>Unit) {
    try {
      f // this must throw an exception, else fail
      assertTrue(false)
    } catch {
      case ex: UnableToPerformActionException => assertTrue(true)
      case ex: Throwable => throw ex
    }
  }
}
