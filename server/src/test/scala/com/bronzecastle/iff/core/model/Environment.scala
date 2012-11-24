package com.bronzecastle.iff.core.model

import org.junit._
import com.bronzecastle.iff.core.objects.IPersistable
import org.junit.Assert._
import com.bronzecastle.iff.core.model.ModelException.PreconditionFailedException

class Environment(objects: IPersistable*) {
  var U: Universe = null

  @Before
  def startup() {
    U = Universe.startup("mem:test")
    U.persist(objects:_*)
  }

  @After
  def shutdown() {
    U.shutdown()
    U = null
  }

  //
  // helpers
  //
  def assertPreconditionFailed(body: =>Unit) {
    try {
      body // this must throw an exception, else fail
      assertTrue(false)
    } catch {
      case ex: PreconditionFailedException => assertTrue(true)
      case ex: Throwable => throw ex
    }
  }
}
