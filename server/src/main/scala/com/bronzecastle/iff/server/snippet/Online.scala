package com.bronzecastle.iff.server
package snippet

import _root_.scala.xml.NodeSeq

class Online {
  def render(in: NodeSeq): NodeSeq = {
    <div>
      <table>
        { for (item <- 0 until 20) yield <tr><td>Hello {item.toString}</td></tr> }
      </table>
      <table>
        { for (item <- 0 until 5) yield <tr><td>Hello {item.toString}</td></tr> }
      </table>
    </div>
  }
}
