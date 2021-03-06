/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core
package model

import action.{DropAction, TakeAction, PutAction, GoAction}
import objects.{IPlace, IThing, IObject, IPersistable}
import orm._
import scala.Some
import orm.DatabaseException.ObjectNotFoundException
import nlp.{NLP, NounCollection, VerbCollection}

/**
 * master container for all world state
 */
class Universe extends IPersistable {
  id = Universe.ID

  var db: Database = null
  val nlp = new NLP

  //
  // starts up the universe
  //
  def startup(db: Database) {
    this.db = db
    Universe.tls.set(this)

    // create $NOWHERE if it does not exist
    persist(IPlace.nowhere)

    // initialize vocabulary for verbs
    register(
      DropAction,
      GoAction,
      PutAction,
      TakeAction
    )
  }

  //
  // closes everything down
  //
  def shutdown() {
    Universe.tls.set(null)
    db.shutdown()
  }

  //
  // bring new objects into existence
  //
  def register(objects: IObject*) {
    // save to database, if persistable
    persist(objects.filter(_.isInstanceOf[IPersistable]).map(_.asInstanceOf[IPersistable]):_*)

    // register vocabulary
    nlp.register(objects:_*)
  }

  //
  // persistence
  //
  def getOption[T <: IPersistable](id: String): Option[T] = {
    LoadPersistable(db,id).map((x)=>x.asInstanceOf[T])
  }

  // get[Type]() -> get[Type]("Type") for the common case where one class := one object
  def get[T <: IPersistable]()(implicit m: Manifest[T]): T = get(m.erasure.getSimpleName)

  // explicit id of type T, for example goblin#1->#5 of type goblin
  def get[T <: IPersistable](id: String): T = {
    LoadPersistable(db,id) match {
      case None => throw new ObjectNotFoundException(id)
      case Some(x) => x.asInstanceOf[T]
    }
  }

  def refresh(ob: IPersistable): Boolean = {
    LoadPersistable(db,ob.id,ob) match {
      case None => false
      case Some(x) => true
    }
  }

  /**
   * persists all arguments or none at all.
   *
   * @param obs objects to persist
   * @return true on success. on failure, caller must roll back transaction
   */
  def persist(obs: IPersistable*): Boolean = {
    db.joinTransaction {
      for (ob <- obs) {
        val b = SavePersistable(db,ob)
        if (!b) { return false }
      }
    }
    true
  }

  /**
   * debug aid - lists everything in the universe
   */
  def logInventory() {
    for (item <- DAO.listAll(db)) {
      val sb = new StringBuilder
      sb.append("["+item.getClass.getSimpleName+"] ")
      item match {
        case thing: IThing => sb.append(item.id+": loc="+thing.location)
        case any: IObject => sb.append(item.id)
      }
      Core.LOG.info(sb.toString())
    }
  }

  /**
   * list all objects directly related to the given location
   */
  def listByLocation(location: String) = DAO.listByLocation(db,location)
}

object Universe {
  val ID = "$UNIVERSE"

  protected val tls = new InheritableThreadLocal[Universe]()

  def apply() = tls.get()

  def startup(name: String): Universe = { // use mem:name to persist universe in memory
    val db = new Database(name)
    Registry.createTables(db)
    val U = LoadPersistable(db,ID).getOrElse(new Universe).asInstanceOf[Universe]
    U.startup(db)
    U.persist(U)
    U
  }
}


