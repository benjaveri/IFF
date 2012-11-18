package com.bronzecastle.iff.server

import org.junit._
import Assert._

@Test
class LoginTest extends WebClientEnvironment {
  @Test
  def hello() {
    val t200 = GET("http://localhost:8080/server/index.html")
    assertTrue(200 == t200._1.getStatusCode)
    val t404 = GET("http://localhost:8080/server/notfound.html")
    assertTrue(404 == t404._1.getStatusCode)
  }
}
