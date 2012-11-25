package com.bronzecastle.iff.core.objects

import com.bronzecastle.iff.core.model.ModelException.{UnableToTravelException, UnableToPerformActionException}
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.Relation.Direction

/**
 * superclass for all verbs
 */
class IAction {
  /**
   * direct action, like take bottle
   *  invoke via actor (actor.act(IAction,...)
   */
  protected[core] def act(actor: IActor,ob: IObject) {
    throw new UnableToPerformActionException
  }

  /**
   * relational action, like put lamp on table
   *  invoke via actor (actor.act(IAction,...)
   */
  protected[core] def act(actor: IActor,ob1: IObject,rel: Relation,ob2: IObject) {
    throw new UnableToPerformActionException
  }

  /**
   * travel action, like go north or enter dungeon (expressed as 'go enter')
   *  invoke via actor (actor.act(IAction,...)
   */
  protected[core] def travel(actor: IActor,dir: Direction) {
    throw new UnableToTravelException
  }
}
