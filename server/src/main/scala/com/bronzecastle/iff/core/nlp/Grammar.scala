package com.bronzecastle.iff.core.nlp

import util.parsing.combinator.RegexParsers
import com.bronzecastle.iff.core.Relation
import com.bronzecastle.iff.core.objects.{IObject, IAction}

/**
 * grammatical constructs we understand
 */
object Grammar extends RegexParsers {
  trait Phrase
  case class V(v: IAction) extends Phrase
  case class VO(v: IAction,o: Iterable[IObject]) extends Phrase
  case class VORO(v: IAction,o1: Iterable[IObject],r: Relation,o2: Iterable[IObject]) extends Phrase

  val EOS = "[.]".r
  val EOP = "[,]".r | "[.]".r | "and"
  val THE = "the"

  val nlp = new InheritableThreadLocal[NLP]()

  def verb = nlp.get.verbs.list.toSeq.sorted(Ordering[String].reverse).map(literal).reduce((a,b)=>{a|b})
  def obj3ct = opt(THE) ~> nlp.get.nouns.list.toSeq.sorted(Ordering[String].reverse).map(literal).reduce((a,b)=>{a|b})
  def relation = Relation.all().map(_.toString.toLowerCase).toSeq.sorted(Ordering[String].reverse).map(literal).reduce((a,b)=>{a|b})

  def toRel(r: String) = Relation.all().filter(_.toString.toLowerCase==r).head
  def toVerb(v: String) = nlp.get.verbs.apply(v).get
  def toNoun(n: String) = nlp.get.nouns.apply(n).get

  def verbPhrase = verb  ^^ {
    case v => V(toVerb(v))
  }
  def verbObjectPhrase = verb ~ obj3ct ^^ {
    case v ~ o => VO(toVerb(v),toNoun(o))
  }
  def verbObjectRelObjectPhrase = verb ~ obj3ct ~ relation ~ obj3ct ^^ {
    case v ~ o1 ~ r ~ o2 => VORO(toVerb(v),toNoun(o1),toRel(r),toNoun(o2))
  }
  def phrase = verbObjectRelObjectPhrase | verbObjectPhrase | verbPhrase
  def sentence = rep1sep(phrase,EOP) <~ opt(EOS) ~ "$".r

  def parse(n: NLP,s: String): ParseResult[List[Grammar.Phrase]] = {
    try {
      nlp.set(n)
      parse(sentence,s)
    } finally {
      nlp.set(null)
    }
  }
}
