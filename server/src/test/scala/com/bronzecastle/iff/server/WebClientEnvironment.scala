package com.bronzecastle.iff.server

import org.junit.{After, Before}
import org.mortbay.jetty.nio.SelectChannelConnector
import org.mortbay.jetty.Server
import org.mortbay.jetty.webapp.WebAppContext
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod

/**
 * jetty based unit test environment
 */
class WebClientEnvironment {
  val server = new Server

  @Before
  def startup() {
    val scc = new SelectChannelConnector
    scc.setPort(8080)
    server.setConnectors(Array(scc))

    val context = new WebAppContext()
    context.setServer(server)
    context.setContextPath("/server")
    context.setWar("src/main/webapp")

    server.addHandler(context)
    server.start()
  }

  @After
  def shutdown() {
    server.stop()
    server.join()
  }

  def GET(url: String) = {
    val client = new HttpClient()
    val method = new GetMethod(url)
    try {
      client.executeMethod(method)
      val body = method.getResponseBody
      (method.getStatusLine,body)
    } finally {
      method.releaseConnection()
    }
  }
}
