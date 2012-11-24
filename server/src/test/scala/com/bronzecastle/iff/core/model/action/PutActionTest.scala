package com.bronzecastle.iff.core.model.action

import org.junit._
import Assert._
import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.Environment

@Test
class PutActionTest
  extends Environment(
    new Clown,
    new Table,new Box,
    new Remote,new Candle,new Cup,new GoldBar,new Balloon
  )
{

  @Test
  def testPutAndDrop() {
    // put remote on table
    val actor = U.get[Clown]
    val table = U.get[Table]
    val remote = U.get[Remote]
    actor.act(PutAction,remote,Relation.On,table)
    assertTrue(remote.location == table.ID)
    assertTrue(remote.relation == Relation.On)
    assertTrue(table.totalWeight == Seq(table,remote).map(_.weight).sum)
    assertTrue(table.totalBulk == Seq(table,remote).map(_.bulk).sum)

    // put box on table
    val box = U.get[Box]
    actor.act(PutAction,box,Relation.On,table)
    assertTrue(table.totalWeight == Seq(table,remote,box).map(_.weight).sum)
    assertTrue(table.totalBulk == Seq(table,remote,box).map(_.bulk).sum)

    // try to put balloon on table, but table too full
    val balloon = U.get[Balloon]
    assertPreconditionFailed {
      actor.act(PutAction,balloon,Relation.On,table)
    }

    // put cup under table and assert it makes no difference to weight & size
    val cup = U.get[Cup]
    actor.act(PutAction,cup,Relation.Under,table)
    assertTrue(table.totalWeight == Seq(table,remote,box).map(_.weight).sum)
    assertTrue(table.totalBulk == Seq(table,remote,box).map(_.bulk).sum)

    // we cannot put anything inside table
    assertPreconditionFailed {
      actor.act(PutAction,balloon,Relation.In,table)
    }
  }
}

class Clown extends IPersistable with IActor {
  override def maxCarryWeight = 5
  override def maxCarrySpace = 5
}

class Table extends IPersistable with ISurface with ICover with IFixture {
  override def weight = 100
  override def capacityOn = 5
  override def capacityUnder = 5
}

class Box extends IPersistable with IContainer {
  override def weight = 3
  override def bulk = 3
  override def capacityInside = 3
}

class Remote extends IPersistable with IThing {
  override def weight = 1
  override def bulk = 1
}

class Candle extends IPersistable with IThing {
  override def weight = 1
  override def bulk = 1
}

class Cup extends IPersistable with IThing {
  override def weight = 1
  override def bulk = 1
}

class GoldBar extends IPersistable with IThing {
  override def weight = 3
  override def bulk = 1
}

class Balloon extends IPersistable with IThing {
  override def weight = 1
  override def bulk = 3
}

