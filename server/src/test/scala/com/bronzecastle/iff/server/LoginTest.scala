package com.bronzecastle.iff.server

import org.junit.Test
import org.junit.Assert._

@Test
class LoginTest extends WebClientEnvironment {
  @Test
  def hello() {
    assertTrue(200 == GET("http://localhost:8080/server/index.html")._1.getStatusCode)
    assertTrue(404 == GET("http://localhost:8080/server/notfound.html")._1.getStatusCode)
  }
}
