package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.model.ModelException.UnableToPerformActionException
import com.bronzecastle.iff.core.Relation

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
