/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.model.Persistent
import java.io.{InputStream, DataInputStream, DataOutputStream, OutputStream}
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

trait IPersistable {
  // persistence: row id and generation number
  private var gen = -1L
  def getGenerationNumber = gen
  def setGenerationNumber(g: Long) { gen = g }

  // required instance name
  def index(): String

  // serialization
  def serialize(os: OutputStream) {
    val stream = new DataOutputStream(os)
    try {
      // emit class
      val clazz = getClass
      val classname = clazz.getName
      stream.writeUTF(classname)
      // emit persistent fields
      for (field <- clazz.getDeclaredFields) {
        // look for @Persistent annotation
        val a = field.getAnnotation(classOf[Persistent])
        if (a != null) {
          // emit field tag
          stream.writeByte(IPersistable.TAG_FIELD)

          // emit field name
          stream.writeUTF(field.getName)

          // emit field value
          field.setAccessible(true)
          val typename = field.getType.getName
          if (IPersistable.SERIALIZERS.contains(typename)) {
            IPersistable.SERIALIZERS(typename)(stream,field,this)
          } else {
            throw new RuntimeException("Cannot serialize type "+typename+". Add to SERIALIZERS")
          }
        }
      }
      stream.writeByte(IPersistable.TAG_END)
    } finally {
      stream.close()
    }
  }
}

object IPersistable {
  // serialization
  def getInstance(is: InputStream): IPersistable = {
    val stream = new DataInputStream(is)
    try {
      // read and marshal class
      var className = stream.readUTF()
      if (CLASS_REMAPPER.contains(className)) className = CLASS_REMAPPER.get(className)
      val clazz = Class.forName(className)
      val ob = clazz.newInstance().asInstanceOf[IPersistable]

      // load persistent fields
      while (true) {
        // get tag
        val tag = stream.readByte()
        if (tag == TAG_END) return ob

        // read field name
        val fieldname = stream.readUTF()
        val field = clazz.getDeclaredField(fieldname)

        // read field value
        field.setAccessible(true)
        val typename = field.getType.getName
        if (IPersistable.DESERIALIZERS.contains(typename)) {
          IPersistable.DESERIALIZERS(typename)(stream,field,ob)
        } else {
          throw new RuntimeException("Cannot deserialize type "+typename+". Add to DESERIALIZERS")
        }
      }
      throw new RuntimeException("Unreachable code reached!")
    } finally {
      stream.close()
    }
  }

  //
  // remap class names here - this allows you to refactor your code
  //  and maintain reverse compatibility with older databases
  //
  val CLASS_REMAPPER = new ConcurrentHashMap[String,String]()

  //
  // stream tags
  //
  protected val TAG_FIELD: Byte = 1
  protected val TAG_END: Byte   = 2

  //
  // type registry - can be extended dynamically
  //
  val SERIALIZERS = collection.mutable.Map(
    "boolean" -> ((s: DataOutputStream,f: Field,ob: Any) => { s.writeBoolean(f.getBoolean(ob)) }),
    "int" -> ((s: DataOutputStream,f: Field,ob: Any) => { s.writeInt(f.getInt(ob)) }),
    "long" -> ((s: DataOutputStream,f: Field,ob: Any) => { s.writeLong(f.getLong(ob)) }),
    "java.lang.String" -> ((s: DataOutputStream,f: Field,ob: Any) => { s.writeUTF(f.get(ob).asInstanceOf[String]) })
  )

  val DESERIALIZERS = collection.mutable.Map(
    "boolean" -> ((s: DataInputStream,f: Field,ob: Any) => { f.setBoolean(ob,s.readBoolean()) }),
    "int" -> ((s: DataInputStream,f: Field,ob: Any) => { f.setInt(ob,s.readInt()) }),
    "long" -> ((s: DataInputStream,f: Field,ob: Any) => { f.setLong(ob,s.readLong()) }),
    "java.lang.String" -> ((s: DataInputStream,f: Field,ob: Any) => { f.set(ob,s.readUTF()) })
  )
}
