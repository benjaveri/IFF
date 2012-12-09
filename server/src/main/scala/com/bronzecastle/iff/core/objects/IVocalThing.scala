package com.bronzecastle.iff.core.objects

/**
 * the vocabulary for an object
 */
trait IVocalThing {
  def desc: Option[String] = None // "small/tiny/glass bottle/vial"

  val adjectives: Set[String] = {
    (for (d <- desc) yield {
      val a = d.toLowerCase.split(" ")
      if (a.size > 1) a.head.split("/").toSet else Set[String]()
    }).headOption.getOrElse(Set[String]())
  }

  val nouns: Set[String] = {
    (for (d <- desc) yield {
      val a = d.toLowerCase.split(" ")
      a.last.split("/").toSet
    }).headOption.getOrElse(Set[String]())
  }
}
