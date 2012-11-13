/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core
package model

import com.bronzecastle.iff.core.objects.IPersistable
import com.bronzecastle.iff.core.orm.{Database, Registry, SavePersistable, LoadPersistable}
import objects.IPersistable._

/**
 * master container for all world state
 */
class Universe extends IPersistable {
  index = Universe.INDEX

  var db: Database = null

  //
  // starts up the universe
  //
  def startup(db: Database) {
    this.db = db
    Universe.tls.set(this)
  }

  //
  // closes everything down
  //
  def shutdown() {
    db.shutdown()
    Universe.tls.set(null)
  }

  //
  // bring new objects into existence
  //
  def register(objects: IPersistable*) {

  }

  //
  // persistence
  //
  def getInstanceOption(index: String): Option[IPersistable] = {
    LoadPersistable(db,index)
  }

  def getInstance(index: String): IPersistable = {
    LoadPersistable(db,index) match {
      case None => throw new ObjectNotFoundException(index)
      case Some(x) => x
    }
  }

  def getInstanceOption[T <: IPersistable](clazz: Class[T]): Option[T] = {
    val index = indexOf(clazz)
    LoadPersistable(db,index).map((x)=>x.asInstanceOf[T])
  }

  def getInstance[T <: IPersistable](clazz: Class[T]): T = {
    val index = indexOf(clazz)
    LoadPersistable(db,index) match {
      case None => throw new ObjectNotFoundException(index)
      case Some(x) => x.asInstanceOf[T]
    }
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

  protected val tls = new InheritableThreadLocal[Universe]()

  def apply() = tls.get()

  def startup(name: String): Universe = { // use mem:name to persist universe in memory
    val db = new Database(name)
    Registry.createTables(db)
    val U = LoadPersistable(db,INDEX).getOrElse(new Universe).asInstanceOf[Universe]
    U.startup(db)
    U.persist(U)
    U
  }
}

class ObjectNotFoundException(val index: String) extends Exception(index+" does not exist in this universe")

