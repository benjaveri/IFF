/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.model

import com.bronzecastle.iff.core.objects.IPersistable
import scala.Some
import com.bronzecastle.iff.core.orm.{Database, Registry, SavePersistable, LoadPersistable}

/**
 * master container for all world state
 */
class Universe extends IPersistable {
  index = Universe.INDEX

  var db: Database = null

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
  // persistence
  //
  def getInstance(index: String): Option[IPersistable] = {
    LoadPersistable(db,index)
  }
  def refresh(ob: IPersistable): Boolean = {
    LoadPersistable(db,ob.index,ob) match {
      case None => false
      case Some(x) => true
    }
  }
  def persist(ob: IPersistable): Boolean = {
    SavePersistable(db,ob)
  }
}

object Universe {
  val INDEX = "$UNIVERSE"

  def startup(name: String): Universe = { // use mem:name to persist universe in memory
    val db = new Database(name)
    Registry.createTables(db)
    val U = LoadPersistable(db,INDEX).getOrElse(new Universe).asInstanceOf[Universe]
    U.init(db)
    U.startup()
    U.persist(U)
    U
  }
}
