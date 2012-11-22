package com.bronzecastle.iff.core.model.action

import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.model.Universe
import com.bronzecastle.iff.core.model.ModelException._
import com.bronzecastle.iff.core.orm.DatabaseException.UpdateFailedCanRetryException

case object DropAction extends IAction {
  override protected[core] def act(actor: IActor,ob: IObject) {
    Universe().db.joinTransaction {
      //
      // preconditions
      //

      // type check
      if (
        !actor.isInstanceOf[IPersistable] ||
        !ob.isInstanceOf[IPersistable] ||
        !ob.isInstanceOf[IThing]
      ) throw new ObjectTypeMismatchException

      // objects must exist
      if (
        !Universe().refresh(actor.asInstanceOf[IPersistable]) ||
        !Universe().refresh(ob.asInstanceOf[IPersistable])
      ) throw new ObjectDoesNotExistException

      // actor must possess object
      val thing = ob.asInstanceOf[IThing]
      if (thing.location != actor.ID)
        throw new ObjectNotAccessibleException

      // object must be mobile (e.g. an fixture on an actor, like his nose, may not be mobile)
      if (thing.isFixture) throw new ObjectNotMobileException

      //
      // perform action
      //
      thing.location = actor.location
      thing.relation = actor.relation
      actor.totalCarryWeight -= thing.totalWeight
      actor.totalCarrySpace -= thing.totalBulk

      //
      // persist
      //
      if (!Universe().persist(
        thing.asInstanceOf[IPersistable],
        actor.asInstanceOf[IPersistable]
      )) {
        // preconditions changed while setting up, caller may retry after rolling back
        throw new UpdateFailedCanRetryException()
      }
    }
  }
}

