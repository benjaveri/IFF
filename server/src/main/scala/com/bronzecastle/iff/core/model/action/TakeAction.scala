package com.bronzecastle.iff.core.model.action

import com.bronzecastle.iff.core.model.ModelException._
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.Universe
import com.bronzecastle.iff.core.orm.DatabaseException.UpdateFailedCanRetryException
import com.bronzecastle.iff.core.objects._

object TakeAction extends IAction {
  override protected[core] def act(actor: IActor,ob: IObject) {
    Universe().db.joinTransaction {
      //
      // preconditions
      //

      // objects must still exist
      if (
        !actor.isInstanceOf[IPersistable] ||
          !Universe().refresh(actor.asInstanceOf[IPersistable]) ||
          !ob.isInstanceOf[IPersistable] ||
          !ob.isInstanceOf[IThing] ||
          !Universe().refresh(ob.asInstanceOf[IPersistable])
      ) throw new ObjectDoesNotExistException

      // object must be accessible to actor
      val thing = ob.asInstanceOf[IThing]
      if (!actor.canAccess(thing))
        throw new ObjectNotAccessibleException

      // object must pass mobility, weight & bulk checks
      if (thing.isFixture) throw new ObjectNotMobileException
      val totalWeight = thing.weight + actor.totalCarryWeight
      if (totalWeight > actor.maxCarryWeight) throw new ObjectTooBigException
      val totalBulk = thing.bulk + actor.totalCarrySpace
      if (totalBulk > actor.maxCarrySpace) throw new ObjectTooBigException

      //
      // perform action
      //
      thing.location = actor.ID
      thing.relation = Relation.Carrying
      actor.totalCarryWeight += thing.weight
      actor.totalCarrySpace += thing.bulk

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


