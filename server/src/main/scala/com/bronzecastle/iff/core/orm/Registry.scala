/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm


/**
 * central registry for orm related things
 */
object Registry {
  //
  // remap class names here - this allows you to refactor your code
  //  and maintain reverse compatibility with older databases
  //
  val CLASS_REMAPPER = new collection.mutable.HashMap[String,String]()

  //
  // serialization stream tags
  //
  val TAG_FIELD: Byte = 1
  val TAG_END: Byte   = 2

  //
  // create tables (to extend, override and use ALTER TABLE after calling super)
  //
  def createTables(db: Database) {
    db.joinTransaction {
      Connection().executeStatement(
        """
          |CREATE TABLE IF NOT EXISTS objects (
          |  idx VARCHAR(64) PRIMARY KEY,
          |  gen BIGINT,
          |  cls TEXT,
          |  loc VARCHAR(64) DEFAULT NULL,
          |  state BLOB
          |)
        """.stripMargin
      )
      Connection().executeStatement(
        "CREATE INDEX IF NOT EXISTS objects_loc_index ON objects(loc)"
      )
    }
  }
}
