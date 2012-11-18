package com.bronzecastle.iff.core.orm

object DatabaseException {
  class ObjectNotFoundException(val index: String) extends Exception(index+" does not exist in this universe")
  class UpdateFailedCanRetryException extends Exception
}
