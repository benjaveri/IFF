package com.bronzecastle.iff.core.model

object ModelException {
  class UnableToPerformActionException extends Exception
  class ObjectDoesNotExistException extends UnableToPerformActionException
  class ObjectNotAccessibleException extends UnableToPerformActionException


  class UnableToTravelException extends UnableToPerformActionException
}