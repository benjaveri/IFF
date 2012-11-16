/*
 * Copyright (c) 2010-2012 by Ben de Waal. All Rights Reserved.
 *
 * This code is licensed under GPLv3. Other licenses are available directly from the author.
 *
 * No liability is assumed for whatever purpose, intended or unintended.
 */

package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.orm.Persistent
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.Universe

/**
 * a thing that can be in IPlace
 */
trait IThing extends IObject {
  //
  // state
  //

  // how this thing relates to location (in, under, on, etc.)
  @Persistent var relation: Relation = Relation.In
  // where this thing is
  @Persistent(column="loc") var location = IPlace.NOWHERE


  //
  // reflection
  //

  /**
   * isInRoom
   *
   * Evaluates whether this IThing is directly on indirectly
   *  in the given IPlace
   *
   * @param room the IPlace of interest
   * @return true is this is in the room, or false if not
   */
  def isInRoom(room: IPlace): Boolean = {
    Universe().getOption[IPersistable](location) match {
      case None => false
      case Some(inst) => inst match {
        case thing: IThing => thing.isInRoom(room)
        case place: IPlace => IPersistable.idOf(place) == IPersistable.idOf(room)
        case _ => false
      }
    }
  }
}
