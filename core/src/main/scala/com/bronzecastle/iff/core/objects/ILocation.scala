package com.bronzecastle.iff.core.objects

/**
 * a place characters can visit
 */
trait ILocation extends IObject {
  def shortDesc(): String
  def fullDesc(): String
}
