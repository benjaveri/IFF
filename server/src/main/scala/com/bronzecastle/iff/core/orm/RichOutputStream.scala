/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.orm

import java.io.{OutputStream, DataOutputStream}
import com.bronzecastle.iff.core.Relation

class RichOutputStream(os: OutputStream) extends DataOutputStream(os) {
  // writes to stream in native format. not preserving type information in stream
  def writeNative(ob: Any) {
    ob match {
      case x: Boolean => writeBoolean(x)
      case x: Int => writeInt(x)
      case x: Long => writeLong(x)
      case x: String => writeUTF(x)
      case x: Relation => Relation.write(this,x)
      case _ => throw new RuntimeException("Case not implemented")
    }
  }
}
