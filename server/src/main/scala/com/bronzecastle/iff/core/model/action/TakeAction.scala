package com.bronzecastle.iff.core.model.action

import com.bronzecastle.iff.core.model.ModelException._
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.Universe
import com.bronzecastle.iff.core.orm.DatabaseException.UpdateFailedCanRetryException
import com.bronzecastle.iff.core.objects._

object TakeAction extends IAction {
  override def verb: Option[String] = Some("take")

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

      // object must be accessible to actor
      val thing = ob.asInstanceOf[IThing]
      if (!actor.canAccess(thing))
        throw new ObjectNotAccessibleException

      // object must pass mobility, weight & bulk checks
      if (thing.isFixture) throw new ObjectNotMobileException
      val totalWeight = thing.totalWeight + actor.totalCarryWeight
      if (totalWeight > actor.maxCarryWeight) throw new ObjectTooBigException
      val totalBulk = thing.totalBulk + actor.totalCarrySpace
      if (totalBulk > actor.maxCarrySpace) throw new ObjectTooBigException

      //
      // perform action
      //
      thing.location = actor.ID
      thing.relation = Relation.Carrying

      //
      // persist
      //
      if (!Universe().persist(
            thing.asInstanceOf[IPersistable]
      )) {
        // preconditions changed while setting up, caller may retry after rolling back
        throw new UpdateFailedCanRetryException()
      }
    }
  }
}
