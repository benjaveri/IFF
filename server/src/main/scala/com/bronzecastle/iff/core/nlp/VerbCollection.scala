package com.bronzecastle.iff.core.nlp

import collection.mutable.{HashMap => MutableHashMap}
import com.bronzecastle.iff.core.objects.IAction

class VerbCollection {
  protected val vocab = new MutableHashMap[String,IAction]()

  def addUnique(item: (String,IAction)): Boolean = {
    val key = item._1
    val action = item._2
    vocab.synchronized {
      if (vocab.contains(key)) return false
      vocab += (key -> action)
      true
    }
  }

  def apply(key: String) = {
    vocab.synchronized {
      if (vocab.contains(key)) Some(vocab(key)) else None
    }
  }

}
