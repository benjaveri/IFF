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

  override def index() = Universe.INDEX

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
    ORM.getInstance(db,index)
  }
  def refresh(ob: IPersistable): Boolean = {
    ORM.refresh(db,ob)
  }

  //
  // save to database
  //
  def persist(ob: IPersistable): Boolean = {
    ORM.persist(db,ob)
  }
}

object Universe {
  val INDEX = "$UNIVERSE"

  def startup(name: String): Universe = { // use mem:name to persist universe in memory
    val db = new Database(name)
    val U = if (ORM.createTables(db)) {
      // does not exist
      val U = new Universe()
      U.init(db)
      U.persist(U)
      U
    } else {
      // exists
      val U = ORM.getInstance(db,INDEX).asInstanceOf[Universe]
      U.init(db)
      U
    }
    U.startup()
    U
  }
}
