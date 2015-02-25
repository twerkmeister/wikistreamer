package com.scalableminds.wikistreamer.parser


import scala.collection.immutable.ListMap
import scala.util.matching.Regex

case class WikiPage(
                     title: String,
                     ns: String,
                     id: String,
                     revision: WikiPageRevision
                   ) {

  override def toString = {
    s"WikiPage($id, $title, text = '...')"
  }
}
//todo: make timestamp legit date
case class WikiPageRevision(id: String,
                            parentId: String,
                            timeStamp: String,
                            contributor: Contributor,
                            comment: Option[String],
                            model: String,
                            format: String,
                            text: String,
                            sha1: String) {

  private val sectionRegex =
    """(?ms)^=+ (.*?) =+(.*?)(?=^=|\z)""".r
  private val firstSectionRegex =
    """(?ms)(.*?)^=""".r
  lazy val sections: ListMap[String, String] = {
    ListMap(((firstSectionRegex.findFirstMatchIn(processedText).map{m =>
      Seq("" -> m.group(1))
    }).getOrElse(Seq()) ++
      sectionRegex.findAllMatchIn(processedText).map{m =>
      m.group(1) -> m.group(2)
    }):_*)
  }

  def processedText = text
}

object CleaningRegexes {
  val doubleCurly = """\{\{[^\{]*?\}\}"""
  val singleCurly = """:?\{[^\{]*?\}"""
  val recursiveRegexes = List(doubleCurly, singleCurly)

  val refs = """<ref.*?>.*?<(/ref.*?|ref.*?/)>"""
  val amp = """&amp;(&[a-z]{1,4};)?"""
  val enclosingAngledbrackets = """<(.*?)( .*?)?>(.*?)</\1>"""
  val angledBrackets = """<.*?>"""

  val singleUseRegexes = List(refs, enclosingAngledbrackets, angledBrackets, amp)
}

trait CleanedText extends WikiPageRevision {
  import CleaningRegexes.{recursiveRegexes, singleUseRegexes}
  lazy val cleaned = {
    def loop(text: String, regex: String): String = {
      val res = text.replaceAll(regex, "")
      if(res == text)
        text
      else loop(res, regex)
    }
    val afterRecursive = recursiveRegexes.fold(text)((text, regex) => loop(text, regex))
    singleUseRegexes.fold(afterRecursive)((text, regex) => text.replaceAll(regex, ""))
  }

  override def processedText = cleaned
}

sealed trait Contributor
case class User(username: String, id: String) extends Contributor
case class AnonymousUser(ip: String) extends Contributor