package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.model.ModelException.{ObjectNotAccessibleException, ObjectDoesNotExistException, UnableToPerformActionException}
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.model.Universe
import com.bronzecastle.iff.core.orm.DatabaseException.UpdateFailedCanRetryException

/**
 * superclass for all verbs
 */
class IAction {
  /**
   * direct action, like take bottle or go north
   *  invoke via actor (actor.act(IAction,...)
   */
  protected[core] def act(actor: IActor,ob: IObject) {
    throw new UnableToPerformActionException()
  }


  /**
   * relational action, like put lamp on table
   *  invoke via actor (actor.act(IAction,...)
   */
  protected[core] def act(actor: IActor,ob1: IObject,rel: Relation,ob2: IObject) {
    throw new UnableToPerformActionException()
  }
}

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

      //
      // perform action
      //
      thing.location = actor.ID
      thing.relation = Relation.Carrying

      //
      // persist
      //
      if (!Universe().persist(thing.asInstanceOf[IPersistable])) {
        // preconditions changed while setting up, caller may retry
        throw new UpdateFailedCanRetryException()
      }
    }
  }
}

case object DropAction extends IAction

