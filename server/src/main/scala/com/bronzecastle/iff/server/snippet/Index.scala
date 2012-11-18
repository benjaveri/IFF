package com.bronzecastle.iff.server
package snippet

import _root_.scala.xml.NodeSeq
import net.liftweb.http.S
import net.liftweb.http.js.JsCmds.RedirectTo

class Index {
  def render(in: NodeSeq): NodeSeq = {
    S.appendJs(RedirectTo("/online"))
    <div/>
  }
}
