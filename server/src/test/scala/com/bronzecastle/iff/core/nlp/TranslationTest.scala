package com.bronzecastle.iff.core.nlp

import org.junit.Test
import org.junit.Assert._
import com.bronzecastle.iff.core.model.Environment
import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.nlp.TranslationTest._
import Grammar._
import com.bronzecastle.iff.core.model.action.TakeAction

@Test
class TranslationTest
  extends Environment(
    new Room,new Me,
    new Apple,new Bottle
  )
{
  def x(nlp: NLP,s: String,a: IAction,o: String) { nlp.parse(s) match {
    case Success(List(VO(v,set)),next) => {
      assertTrue(v == a)
      assertTrue(set.head.ID == o)
    }
    case Failure(m,next) => fail(m)
    case e => fail("did not expect "+e.toString)
  }}


  @Test
  def testPreprocessor() {
    val nlp = U.nlp

    x(nlp,"take bottle",TakeAction,"Bottle")


/*

    println()
    println(nlp.parse("take bottle and drop the apple."))
    println(nlp.parse("take bottle and take the bottle and take bottle on bottle."))
    println(nlp.parse("put bottle and go."))
    println(nlp.parse("go bottle, drop apple"))
    / *
    assertTrue(t=="take/bottle")
    val t = nlp.parse("  take the yellow bottle, and put it on the table. go north")//.mkString("/")
    println(t)
    assertTrue(t=="take/yellow/bottle/and/put/it/on/table/./go/north")
    */
  }

  @Test
  def testLiteralSentence1() {
    val x = "take bottle"
  }
}

object TranslationTest {
  class Room extends IPersistable with IPlace

  class Me extends IPersistable with IActor {
    location = "Room"
  }

  class Apple extends IPersistable with IThing {
    override def desc = Some("red apple")
    location = "Room"
  }

  class Bottle extends IPersistable with IThing {
    override def desc = Some("green bottle")
    location = "Room"
  }
}