/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import com.bronzecastle.iff.core.objects.IPersistable
import java.sql.ResultSet

/**
 * data access helpers
 */
object DAO {
  /**
   * list all objects in database
   */
  def listAll(db: Database): Iterator[IPersistable] = {
    db.joinTransaction {
      asPersistableIterator(db,Connection().executeQuery(
        "SELECT idx FROM objects"
      ))
    }
  }

  /**
   * list all objects directly related to given location
   */
  def listByLocation(db: Database,location: String): Iterator[IPersistable] = {
    db.joinTransaction {
      asPersistableIterator(db,Connection().executeQuery(
        "SELECT idx FROM objects WHERE loc=?",location
      ))
    }
  }

  //
  // helpers
  //
  private def asPersistableIterator(db: Database,rs: ResultSet): Iterator[IPersistable] = {
    new Iterator[IPersistable]{
      def hasNext = rs.next()
      def next() = LoadPersistable(db,rs.getString(1)).get
    }
  }
}
