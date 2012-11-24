/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import java.io.{InputStream, DataInputStream}
import com.bronzecastle.iff.core.Relation

class RichInputStream(is: InputStream) extends DataInputStream(is){
  def readNative(typename: String): Any = {
    typename match {
      case "boolean" => readBoolean()
      case "int" => readInt()
      case "long" => readLong()
      case "java.lang.String" => readUTF()
      case "com.bronzecastle.iff.core.Relation" => Relation.read(this)
      case _ => throw new RuntimeException("Case not implemented")
    }
  }
}
