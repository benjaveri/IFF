package com.bronzecastle.iff.core.model.action

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects.{IActor, IPortal, IPlace, IPersistable}
import com.bronzecastle.iff.core.model.{ModelException, Universe, Environment}
import com.bronzecastle.iff.core.{Relation, Direction}
import GoActionTest._
import com.bronzecastle.iff.core.model.ModelException.NoExitException

@Test
class GoActionTest
  extends Environment(
    new Me,
    new A,new B,new C,
    new Portal
  )
{
  @Test
  def testSimpleTravel() {
    val actor = U.get[Me]
    actor.travel(GoAction,Direction.East)
  }

  @Test
  def testPortalTravel() {
    val actor = U.get[Me]
    actor.travel(GoAction,Direction.East)
    actor.travel(GoAction,Direction.East)
  }
}


/*

  +-----+     +-----+     +-----+
  |     |     |     |     |     |
  |  A  |<--->|  B  |<-#->|  C  |
  |     |     |     |  ^  |     |
  +-----+     +-----+  |  +-----+
                       |
                     Portal

*/
object GoActionTest {
  class Me extends IPersistable with IActor {
    location = "A"
  }

  class A extends IPersistable with IPlace {
    override def exits = {
      case Direction.East => "B"
    }
  }

  class B extends IPersistable with IPlace {
    override def exits = {
      case Direction.West => "A"
      case Direction.East => "Portal"
    }
  }
  class Portal extends IPersistable with IPortal {
    // simple transparent portal
    override def allowTravel(actor: IActor,from: IPlace) = { from.ID match {
      case "B" => Universe().get[C]
      case "C" => Universe().get[B]
      case _ => throw new NoExitException
    }}
  }
  class C extends IPersistable with IPlace {
    override def exits = {
      case Direction.West => "Portal"
    }
  }
}