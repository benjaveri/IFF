package com.bronzecastle.iff.core.model.action

import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.Relation.Direction
import com.bronzecastle.iff.core.model.ModelException._
import com.bronzecastle.iff.core.model.Universe
import com.bronzecastle.iff.core.orm.DatabaseException.{ObjectNotFoundException, UpdateFailedCanRetryException}
import com.bronzecastle.iff.core.Relation

/**
 * actor travel
 */
object GoAction extends IAction {
  override def verb: Option[String] = Some("go")

  override protected[core] def travel(actor: IActor,dir: Direction) {
    Universe().db.joinTransaction {
      //
      // preconditions
      //

      // type check
      if (
        !actor.isInstanceOf[IPersistable]
      ) throw new ObjectTypeMismatchException

      // objects must exist
      if (
        !Universe().refresh(actor.asInstanceOf[IPersistable])
      ) throw new ObjectDoesNotExistException

      // exit must be defined
      val from = actor.getPlace
      if (
        !from.exits.isDefinedAt(dir)
      ) throw new NoExitException

      // direct exit or resolve when via an IPortal
      var destOption: Option[IPlace] = None
      try {
        Universe().get[IPersistable](from.exits(dir)) match {
          case via: IPortal => {
            val to = via.allowTravel(actor,from)
            destOption = Some(to)
          }
          case to: IPlace => {
            destOption = Some(to)
          }
          case _ => throw new BadDestinationException
        }
      } catch {
        case ex: ObjectNotFoundException => throw new ObjectDoesNotExistException
      }
      if (destOption.isEmpty) throw new BadDestinationException

      // dest type check
      val dest = destOption.get
      if (
        !dest.isInstanceOf[IPersistable] ||
        !dest.isInstanceOf[IPlace]
      ) throw new ObjectTypeMismatchException

      //
      // perform action
      //
      actor.location = dest.ID
      actor.relation = Relation.In

      //
      // persist
      //
      if (!Universe().persist(
        actor.asInstanceOf[IPersistable]
      )) {
        // preconditions changed while setting up, caller may retry after rolling back
        throw new UpdateFailedCanRetryException()
      }
    }
  }
}
