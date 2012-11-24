package com.bronzecastle.iff.core.model.action

import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.ModelException._
import com.bronzecastle.iff.core.model.Universe
import com.bronzecastle.iff.core.orm.DatabaseException.UpdateFailedCanRetryException

case object PutAction extends IAction {
  override protected[core] def act(actor: IActor,ob1: IObject,rel: Relation,ob2: IObject) {
    Universe().db.joinTransaction {
      //
      // preconditions
      //

      // type check
      if (
        !actor.isInstanceOf[IPersistable] ||
        !ob1.isInstanceOf[IPersistable] ||
        !ob1.isInstanceOf[IThing] ||
        !ob2.isInstanceOf[IPersistable] ||
        !ob2.isInstanceOf[IThing]
      ) throw new ObjectTypeMismatchException

      // objects must exist
      if (
        !Universe().refresh(actor.asInstanceOf[IPersistable]) ||
        !Universe().refresh(ob1.asInstanceOf[IPersistable]) ||
        !Universe().refresh(ob2.asInstanceOf[IPersistable])
      ) throw new ObjectDoesNotExistException

      // both objects must be accessible
      val thing1 = ob1.asInstanceOf[IThing]
      val thing2 = ob2.asInstanceOf[IThing]
      if (
        !actor.canAccess(thing1) ||
        !actor.canAccess(thing2)
      ) throw new ObjectNotAccessibleException

      // object1 must be mobile and actor must be able to lift it
      if (thing1.isFixture) throw new ObjectNotMobileException
      val totalWeight = thing1.totalWeight + actor.totalCarryWeight
      if (totalWeight > actor.maxCarryWeight) throw new ObjectTooBigException

      // object2 must be a container that supports given relation
      if (!thing2.supportsRelation(rel)) throw new RelationNotSupportedException
      val totalBulk = thing2.totalHoldingBulk(rel) + thing1.totalBulk
      if (totalBulk > thing2.maxHoldingSpace(rel)) throw new ObjectTooBigException

      //
      // perform action
      //
      thing1.location = thing2.ID
      thing1.relation = rel

      //
      // persist
      //
      if (!Universe().persist(
        thing1.asInstanceOf[IPersistable]
      )) {
        // preconditions changed while setting up, caller may retry after rolling back
        throw new UpdateFailedCanRetryException()
      }
    }
  }
}
