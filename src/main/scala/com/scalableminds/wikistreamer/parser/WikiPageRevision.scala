package com.scalableminds.wikistreamer.parser

import scala.collection.immutable.ListMap

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

  val categories: Set[String] = Set.empty[String]

  val links: Array[String] = Array.empty[String]

}

object CleaningRegexes {
  val doubleCurly = """\{\{[^\{]*?\}\};?"""
  val singleCurly = """:?\{[^\{]*?\};?"""
  val recursiveRegexes = List(doubleCurly, singleCurly)

  val refs = """<ref.*?>.*?<(/ref.*?|ref.*?/)>"""
  val amp = """&nbsp;"""
  val enclosingAngledbrackets = """<(.*?)( .*?)?>(.*?)</\1>"""
  val galleries = """(?s)<gallery([^<>]*?)?>.*?</gallery>"""
  val angledBrackets = """<.*?>"""
  val typedSquareBrackets ="""\[\[([^\[\] ]*?):[^\[\]]*?(\[\[[^\[\]]*?\]\])?[^\[\]]*?\]\]"""
  val htmlComment = """(?s)<!--.*?-->"""
  val outlinks = """\[https?://[^\[\]]*?\]"""
  val emptyLines = """(?m)^(:|\*|;)\s*$"""


  val singleUseRegexes = List(refs, enclosingAngledbrackets, angledBrackets, amp, galleries, typedSquareBrackets, htmlComment, emptyLines)
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
  override val categories: Set[String] = categoriesRegex.findAllMatchIn(text).map{ categoryMatch =>
    categoryMatch.group(1)
  }.toSet
}

object LinkRegexes {
  val linkRegex = """\[\[(.*?)\]\]""".r
  val namedLinkRegex = """\[\[([^\[\]]*?)\|(.*?)\]\]""".r
}

trait ExtractLinks extends WikiPageRevision {
  import LinkRegexes._

  override val links: Array[String] = extractLinks(processedText)

  def extractLinks(text: String): Array[String] = {
    (namedLinkRegex.findAllMatchIn(text).map{m => m.group(2)} ++
      linkRegex.findAllMatchIn(text).map{m => m.group(1)}).toArray
  }
}

trait RemoveLinks extends WikiPageRevision {
  import LinkRegexes._

  override def processText(text: String): String = {
    val textWithoutLinks = text.replaceAll(namedLinkRegex.toString, "$2").replaceAll(linkRegex.toString, "$1")
    super.processText(textWithoutLinks)
  }
}

trait RemoveEnumerations extends WikiPageRevision {
  override def processText(text: String): String = {
    val enumerationStarterLine = """(?m)^.*?:\n(?=^[\*#])"""
    val enumeration = """(?m)^:?[\*#].*?$"""
    val textWithoutEnumerations = text.replaceAll(enumerationStarterLine, "").replaceAll(enumeration, "")
    super.processText(textWithoutEnumerations)
  }
}