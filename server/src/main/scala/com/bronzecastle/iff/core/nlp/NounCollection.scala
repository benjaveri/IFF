package com.bronzecastle.iff.core.nlp

import collection.mutable.{HashMap => MutableHashMap}
import collection.mutable.{Set => MutableSet}
import collection.mutable.{HashSet => MutableHashSet}
import com.bronzecastle.iff.core.objects.{IObject, IThing}

class NounCollection {
  protected val vocab = new MutableHashMap[String,MutableSet[IObject]]()

  def +=(item: (String,IObject)) {
    val key = item._1
    val ob = item._2
    vocab.synchronized {
      if (!vocab.contains(key)) vocab += (key -> new MutableHashSet[IObject]())
      vocab(key) += ob
    }
  }

  def ++=(list: Iterable[(String,IObject)]) {
    list.foreach(+=(_))
  }

  def apply(key: String) = {
    vocab.synchronized {
      if (vocab.contains(key)) Some(vocab(key)) else None
    }
  }

  def contains(key: String) = {
    vocab.synchronized {
      vocab.contains(key)
    }
  }

  def list = vocab.keys
}
