/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import org.junit._
import Assert._
import org.apache.log4j.Logger
import Connection.QueryConvertions._
import java.io.File

@Test
class DatabaseTest {
  @Test
  def testPersistInMemory() {
    // create a database in memory
    var db = new Database("mem:test")//;TRACE_LEVEL_SYSTEM_OUT=4")
    createTables(db)
    setProp(db,"a","1")
    setProp(db,"b","2")
    assertTrue("1" == getProp(db,"a").getOrElse("?"))
    assertTrue("2" == getProp(db,"b").getOrElse("?"))
    db.shutdown()

    // after its closed, a new one with the same name should be empty
    db = new Database("mem:test")
    createTables(db)
    assertTrue(0 == db.joinTransaction {
      Connection().querySingle(asLong)("SELECT COUNT(*) FROM prop").getOrElse(-1)
    })
    db.shutdown()
  }

  @Test
  def testPersistInFile() {
    val dbname = "test"
    val filename = dbname+".h2.db" // what h2 is going to put on disk
    try {
      // create a database in memory
      var db = new Database(dbname)//;TRACE_LEVEL_SYSTEM_OUT=4")
      createTables(db)
      setProp(db,"a","1")
      setProp(db,"b","2")
      assertTrue("1" == getProp(db,"a").getOrElse("?"))
      assertTrue("2" == getProp(db,"b").getOrElse("?"))
      db.shutdown()

      assertTrue(new File(filename).exists())

      // after its closed, a new one with the same name should remember everything
      db = new Database(dbname)
      createTables(db)
      assertTrue(2 == db.joinTransaction {
        Connection().querySingle(asLong)("SELECT COUNT(*) FROM prop").getOrElse(-1)
      })
      assertTrue("1" == getProp(db,"a").getOrElse("?"))
      assertTrue("2" == getProp(db,"b").getOrElse("?"))
      db.shutdown()
    } finally {
      new File(filename).delete()
    }
  }

  /*
  @Test
  def testRollbackException() {
    // create a database in memory
    var db = new Database("mem:test;TRACE_LEVEL_SYSTEM_OUT=4")
    createTables(db)
    setProp(db,"a","1")
    setProp(db,"b","2")
    assertTrue("1" == getProp(db,"a").getOrElse("?"))
    assertTrue("2" == getProp(db,"b").getOrElse("?"))

    db.joinTransaction {
      val i = getProp(db,"a").get
      setProp(db,"a",i+i)

      val c2 = db.getConnection()
      try {
        c2.executeUpdate("UPDATE prop SET v=? WHERE k=?","x","a")
        c2.commitTransaction()
      } catch {
        case ex: SQLException => {

          LOG.info(String.format("exception %s %s",ex.getErrorCode.toString,ex.getSQLState),ex)
        }
      }
      db.returnConnection(c2)

      val j = getProp(db,"a").get
      setProp(db,"a",i+j)
    }

    val r = getProp(db,"a").getOrElse("?")
    assertTrue(r=="111")

    db.shutdown()
  }
  */

  //
  // utilties
  //
  def createTables(db: Database) {
    db.joinTransaction {
      Connection().executeStatement(
        """
          |CREATE TABLE IF NOT EXISTS prop (
          |  id BIGINT IDENTITY PRIMARY KEY,
          |  k VARCHAR(64) NOT NULL,
          |  v TEXT NOT NULL
          |)
        """.stripMargin
      )
      Connection().executeStatement(
        """
          |CREATE INDEX IF NOT EXISTS prop_k_index ON prop(k)
        """.stripMargin
      )
    }
  }

  def setProp(db: Database,key: String,value: String) {
    assertTrue (1 == db.joinTransaction {
      Connection().executeUpdate("INSERT INTO prop (k,v) VALUES (?,?)",key,value)
    })
  }

  def getProp(db: Database,key: String): Option[String] = {
    db.joinTransaction {
      Connection().querySingleWithArgs(asString)("SELECT v FROM prop WHERE k=?",Seq(key))
    }
  }
}
