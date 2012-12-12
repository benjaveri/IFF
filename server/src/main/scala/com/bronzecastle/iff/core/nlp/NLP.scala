package com.bronzecastle.iff.core.nlp

import com.bronzecastle.iff.core.objects._
import com.bronzecastle.iff.core.model.ModelException.DuplicateWordException

class NLP {
  val verbs = new VerbCollection
  val nouns = new NounCollection

  /**
   * registers vocabulary for things, places and actions
   *
   * @param objects a list of IObjects to register
   */
  def register(objects: IObject*) {
    objects.foreach((ob)=>{
      ob match {
        case thing: IThing  => {
          for (d <- thing.desc) {
            val words = thing.nouns
            if (0 != words.filter((w)=>nouns.contains(w)).size)
              throw new DuplicateWordException
            nouns ++= words.map((w)=>(w -> thing))
          }
        }
        case place: IPlace => {
          for (d <- place.desc) {
            val words = place.nouns
            if (0 != words.filter((w)=>nouns.contains(w)).size)
              throw new DuplicateWordException
            nouns ++= words.map((w)=>(w -> place))
          }
        }
        case action: IAction => {
          for (verb <- action.verb) {
            if (!verbs.addUnique(verb -> action))
              throw new DuplicateWordException
          }
        }
      }
    })
  }


  def parse(s: String): Grammar.ParseResult[List[Grammar.Phrase]] = {
    Grammar.parse(this,s)
  }
}
