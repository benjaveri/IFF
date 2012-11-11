/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.model

import Connection.QueryConvertions._
import com.bronzecastle.iff.core.objects.IPersistable
import java.io.ByteArrayOutputStream
import javax.sql.rowset.serial.SerialBlob
import java.awt.print.Book


object ORM {
  // marshals an object from the database
  def getInstance(db: Database,index: String): Option[IPersistable] = {
    db.joinTransaction {
      Connection().querySingleWithArgs(asLongBlob)(
        "SELECT gen,state FROM objects WHERE idx=?",
        Seq(index)
      )
    } match {
      case None => None
      case Some(row) => {
        val s = row._2.getBinaryStream
        try {
          val o = IPersistable.getInstance(s)
          o.gen = row._1
          Some(o)
        } finally {
          s.close()
        }
      }
    }
  }

  // refreshes an object's state
  def refresh(db: Database,ob: IPersistable): Boolean = {
    db.joinTransaction {
      Connection().querySingleWithArgs(asLongBlob)(
        "SELECT gen,state FROM objects WHERE idx=?",
        Seq(ob.index())
      )
    } match {
      case None => false
      case Some(row) => {
        val s = row._2.getBinaryStream
        try {
          IPersistable.getInstance(s,ob)
          ob.gen = row._1
          true
        } finally {
          s.close()
        }
      }
    }
  }

  // persists an object to database, only if it is up to date
  def persist(db: Database,ob: IPersistable): Boolean = {
    val bos = new ByteArrayOutputStream
    ob.serialize(bos)
    bos.close()
    val blob = new SerialBlob(bos.toByteArray)

    db.joinTransaction {
      Connection().querySingleWithArgs(asLong)(
        "SELECT COUNT(*) FROM objects WHERE idx=?",
        Seq(ob.index())
      ).map((count) => {
        if (count == 0) {
          Connection().executeUpdate(
            "INSERT INTO objects (idx,gen,state) VALUES (?,?,?)",
            ob.index(),
            ob.gen+1,
            blob
          )
        } else {
          Connection().executeUpdate(
            "UPDATE objects SET gen=?,state=? WHERE idx=? AND gen=?",
            ob.gen+1,
            blob,
            ob.index(),
            ob.gen
          )
        }
      })
    }.foreach((count) => {
      if (count == 1) {
        ob.gen += 1
        return true
      }
    })
    false
  }

  // creates tables. return true if this is a new database
  def createTables(db: Database): Boolean = {
    db.joinTransaction {
      Connection().executeStatement(
        """
          |CREATE TABLE IF NOT EXISTS objects (
          |  idx VARCHAR(64) PRIMARY KEY,
          |  gen BIGINT,
          |  state BLOB
          |)
        """.stripMargin
      )
      // previously created databases will have at least the $UNIVERSE in it
      Connection().querySingle(asLong)("SELECT COUNT(*) FROM objects")
    }.foreach((count)=>{
      return count == 0
    })
    throw new RuntimeException("Unreachable code reached!")
  }
}
