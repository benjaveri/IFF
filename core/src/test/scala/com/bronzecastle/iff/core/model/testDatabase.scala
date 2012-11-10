package com.bronzecastle.iff.core.model

import org.junit._
import Assert._
import org.apache.log4j.Logger
import Connection.QueryConvertions._

@Test
class testDatabase {
  val LOG = Logger.getLogger(getClass)

  @Test
  def testPersistInMemory {
    // create a database in memory
    var db = new Database("mem:test")//;TRACE_LEVEL_SYSTEM_OUT=4")
    createTables(db)
    setProp(db,"a","1")
    setProp(db,"b","2")

    db.doTransaction{
      Connection().queryMany(asIntStringString)("SELECT * FROM prop").foreach((row)=>{
        LOG.info(row.toString())
      })
    }

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
  def testPersistInFile {

  }

  @Test
  def testConnectionPool {

  }

  @Test
  def testRollbackException {

  }


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
