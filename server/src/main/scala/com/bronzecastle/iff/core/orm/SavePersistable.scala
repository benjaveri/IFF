/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import com.bronzecastle.iff.core.objects.IPersistable
import java.io.ByteArrayOutputStream
import javax.sql.rowset.serial.SerialBlob
import com.bronzecastle.iff.core.orm.Connection.QueryConvertions._
import collection.mutable
import com.bronzecastle.iff.core.UnreachableCodeReachedException

/**
 * persists an IPersistable to database
 */
object SavePersistable {
  def apply(db: Database,ob: IPersistable): Boolean = {
    // signal to object it is about to be persisted
    ob.triggerAboutToPersist()
    ob.gen += 1 // advance gen

    // serialize object to a Map, indexed by column name
    val map = new mutable.HashMap[String,Any]()
    val clazz = ob.getClass
    map += "cls" -> clazz.getName

    // pickle object
    val bos = new ByteArrayOutputStream()
    val stream = new RichOutputStream(bos)
    try {
      // emit persistent fields
      for (field <- clazz.getDeclaredFields) {
        // look for @Persistent annotation
        field.setAccessible(true)
        val a = field.getAnnotation(classOf[Persistent])
        if (a != null) {
          if (a.column().isEmpty) {
            // generic field, serialize to state column
            // emit field tag
            stream.writeByte(Registry.TAG_FIELD)

            // emit field name
            stream.writeUTF(field.getName)

            // emit field value
            val v = field.get(ob)
            stream.writeNative(v)
          } else {
            // field with dedicated column
            map += a.column() -> field.get(ob)
          }
        }
      }
      stream.writeByte(Registry.TAG_END)
    } finally {
      stream.close()
      bos.close()
    }
    map += "state" -> new SerialBlob(bos.toByteArray)

    // write to database
    db.joinTransaction {
      Connection().querySingleWithArgs(asLong)(
        "SELECT COUNT(*) FROM objects WHERE idx=?",
        Seq(ob.id)
      ).map((count) => {
        if (count == 0) {
          val keys = map.keysIterator.mkString(",")
          val places = map.keysIterator.map((key)=>{"?"}).mkString(",")
          val values = map.keysIterator.map((key)=>{map(key)}).toSeq
          Connection().executeUpdate(
            "INSERT INTO objects ("+keys+") VALUES ("+places+")",
            values:_*
          )
        } else {
          val idx = map("idx").asInstanceOf[String]
          val gen = map("gen").asInstanceOf[Long]
          val keys = map.keysIterator.map((key)=>{key+"=?"}).mkString(",")
          val values = map.keysIterator.map((key)=>{map(key)}).toSeq ++ Seq(idx,gen-1)
          Connection().executeUpdate(
            "UPDATE objects SET "+keys+" WHERE idx=? AND gen=?",
            values:_*
          )
        }
      })
    }.foreach((count) => {
      if (count == 1) {
        return true
      } else {
        // fail. roll back generation number
        ob.gen -= 1
        return false
      }
    })
    throw new UnreachableCodeReachedException()
  }
}
