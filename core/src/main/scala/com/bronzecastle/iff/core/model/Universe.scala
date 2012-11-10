package com.bronzecastle.iff.core.model

import com.bronzecastle.iff.core.objects.IObject

/**
 * master container for all world state
 */
class Universe(name: String) { // use mem:name to persist universe in memory
  protected val db = new Database(name)

  def create() {}
  def destroy() {}

  def register(objects: IObject*) {}
}
