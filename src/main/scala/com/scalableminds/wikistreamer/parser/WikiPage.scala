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

  val processedText = processText(text)

  def processText(text: String) = text

  val categories: List[String] = Nil

}

object CleaningRegexes {
  val doubleCurly = """\{\{[^\{]*?\}\}"""
  val singleCurly = """:?\{[^\{]*?\}"""
  val recursiveRegexes = List(doubleCurly, singleCurly)

  val refs = """<ref.*?>.*?<(/ref.*?|ref.*?/)>"""
  val amp = """&nbsp;"""
  val enclosingAngledbrackets = """<(.*?)( .*?)?>(.*?)</\1>"""
  val angledBrackets = """<.*?>"""
  val typedSquareBrackets ="""\[\[([^\[\] ]*?):[^\[\]]*?(\[\[[^\[\]]*?\]\])?[^\[\]]*?\]\]"""
  val htmlComment = """(?s)<!--.*?-->"""
  val outlinks = """\[https?://[^\[\]]*?\]"""

  val singleUseRegexes = List(refs, enclosingAngledbrackets, angledBrackets, amp, typedSquareBrackets, htmlComment, outlinks)
}

trait CleanedText extends WikiPageRevision {
  import CleaningRegexes.{recursiveRegexes, singleUseRegexes}
  def clean(text: String) = {
    def loop(text: String, regex: String): String = {
      val res = text.replaceAll(regex, "")
      if(res == text)
        text
      else loop(res, regex)
    }
    val afterRecursive = recursiveRegexes.fold(text)((text, regex) => loop(text, regex))
    singleUseRegexes.fold(afterRecursive)((text, regex) => text.replaceAll(regex, ""))
  }

  override def processText(text: String) = super.processText(clean(text))
}

trait Categories extends WikiPageRevision {
  val categoriesRegex = """\[\[Kategorie:(.*?)(\|(.*?))?\]\]""".r
  override val categories: List[String] = categoriesRegex.findAllMatchIn(text).map{ categoryMatch =>
    categoryMatch.group(1)
  }.toList
}

trait Links extends WikiPageRevision {
  val linkRegex = """\[\[\(.*?)\]\]""".r
  val namedLinkRegex = """\[\[(.*?)\|(.*?)\]\]""".r


  private val linkMatches = linkRegex.findAllMatchIn(processedText)

  override def processText(text: String) = {
    val namedLinkMatches = namedLinkRegex.findAllMatchIn(processedText).toList
    val textWithoutNamedLinks = namedLinkMatches.fold(text)((text, m) => text.replaceAll(m.group(0), m.group(2)))
    val linkMatches = linkRegex.findAllMatchIn(textWithoutNamedLinks)
//    val textWithoutLinks =
  }
}

sealed trait Contributor
case class User(username: String, id: String) extends Contributor
case class AnonymousUser(ip: String) extends Contributor