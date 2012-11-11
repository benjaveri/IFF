/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.model

import com.bronzecastle.iff.core.objects.{IPersistable, IObject}
import Connection.QueryConvertions._
import java.io.ByteArrayOutputStream
import javax.sql.rowset.serial.SerialBlob

/**
 * master container for all world state
 */
class Universe extends IPersistable {
  var db: Database = null

  def index() = "$UNIVERSE"

  //
  // initialization
  //
  def init(db: Database) {
    this.db = db
  }

  //
  // starts up the universe
  //
  def startup() {
  }

  //
  // closes everything down
  //
  def shutdown() {
    db.shutdown()
  }

  //
  // bring new objects into existence
  //
  def register(objects: IPersistable*) {

  }

  //
  // marshal from database
  //
  def getInstance(index: String): Option[IPersistable] = {
    Universe.getInstance(db,index)
  }

  //
  // save to database
  //
  def persist(ob: IPersistable): Boolean = {
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
            ob.getGenerationNumber+1,
            blob
          )
        } else {
          Connection().executeUpdate(
            "UPDATE objects SET gen=?,state=? WHERE idx=? AND gen=?",
            ob.getGenerationNumber+1,
            blob,
            ob.index(),
            ob.getGenerationNumber
          )
        }
      })
    }.foreach((count) => {
      if (count == 1) {
        ob.setGenerationNumber(ob.getGenerationNumber+1)
        return true
      }
    })
    false
  }
}

object Universe {
  def getInstance(db: Database,index: String): Option[IPersistable] = {
    db.joinTransaction {
      Connection().querySingleWithArgs(asLongBlob)(
        "SELECT gen,state FROM objects WHERE idx=?",
        index
      )
    } match {
      case None => None
      case Some(row) => {
        val s = row._2.getBinaryStream
        try {
          val o = IPersistable.getInstance(s)
          o.setGenerationNumber(row._1)
          Some(o)
        } finally {
          s.close()
        }
      }
    }
  }

  def startup(name: String): Universe = { // use mem:name to persist universe in memory
    val db = new Database(name)
    db.doTransaction {
      Connection().executeStatement(
        """
          |CREATE TABLE IF NOT EXISTS objects (
          |  idx VARCHAR(64) PRIMARY KEY,
          |  gen BIGINT,
          |  state BLOB
          |)
        """.stripMargin
      )
      Connection().querySingle(asLong)("SELECT COUNT(*) FROM objects")
    }.foreach((count)=>{
      if (count != 0) {
        // exists
        val U = getInstance(db,"$UNIVERSE").asInstanceOf[Universe]
        U.init(db)
        U.startup()
        return U
      } else {
        // does not exist
        val U = new Universe()
        U.init(db)
        U.persist(U)
        U.startup()
        return U
      }
    })
    throw new RuntimeException("Unreachable code reached!")
  }
}