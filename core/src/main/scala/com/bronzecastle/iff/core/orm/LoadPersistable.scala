/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import com.bronzecastle.iff.core.objects.IPersistable
import com.bronzecastle.iff.core.UnreachableCodeReachedException

/**
 * loads (or refreshes) an IPersistable from database
 */
object LoadPersistable {
  def apply(db: Database,idx: String,withInstance: IPersistable = null): Option[IPersistable] = {
    // read database entry
    val row = db.joinTransaction {
      Connection().executeQuery(
        "SELECT * FROM objects WHERE idx=?",
        idx
      )
    }
    if (!row.next()) return None

    // create a new instance of the object, if withInstance is null. withInstance
    //  will be non-null when you want to refresh an existing instance
    var ob = withInstance
    if (ob == null) {
      var className = row.getString("cls")
      if (Registry.CLASS_REMAPPER.contains(className)) className = Registry.CLASS_REMAPPER(className)
      val clazz = Class.forName(className)
      ob = clazz.newInstance().asInstanceOf[IPersistable]
    }
    if (ob == null) return None
    var clazz = ob.getClass

    // unpickle named columns
    for (field <- clazz.getDeclaredFields) {
      // look for @Persistent annotation
      field.setAccessible(true)
      val a = field.getAnnotation(classOf[Persistent])
      if ((a != null) && !a.column().isEmpty) {
        field.set(ob,row.getObject(a.column()))
      }
    }

    // unpickle general state
    val blob = row.getBlob("state")
    val bis = blob.getBinaryStream
    val stream = new RichInputStream(bis)
    try {
      // load persistent fields
      while (true) {
        // get tag
        val tag = stream.readByte()
        if (tag == Registry.TAG_END) {
          ob.triggerJustLoaded()
          return Some(ob)
        }

        // read field name
        val fieldname = stream.readUTF()
        val field = clazz.getDeclaredField(fieldname)

        // read field value
        field.setAccessible(true)
        val typename = field.getType.getName
        val v = stream.readNative(typename)
        field.set(ob,v)
      }
      throw new UnreachableCodeReachedException()
    } finally {
      stream.close()
      bis.close()
    }
  }
}
