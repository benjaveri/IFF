package com.bronzecastle.iff.core.model

object ModelException {
  class PreconditionFailedException extends Exception
    class UnableToPerformActionException extends PreconditionFailedException
      class ObjectTypeMismatchException extends UnableToPerformActionException
      class ObjectDoesNotExistException extends UnableToPerformActionException
      class ObjectNotAccessibleException extends UnableToPerformActionException
      class ObjectNotMobileException extends UnableToPerformActionException
      class NotEnoughStrengthException extends UnableToPerformActionException // sum of object weight exceeds strength
      class NotEnoughSpaceException extends UnableToPerformActionException // sum of object bulk exceeds available space
      class RelationNotSupportedException extends UnableToPerformActionException

    class UnableToTravelException extends PreconditionFailedException
      class BadDestinationException extends UnableToTravelException
      class NoExitException extends UnableToTravelException

  class DuplicateWordException extends Exception
}