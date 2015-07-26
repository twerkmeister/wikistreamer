package com.scalableminds.wikistreamer.transformers

import com.scalableminds.wikistreamer.parser.{WikiPage, Altered, WikiPageRevision}
import com.scalableminds.wikistreamer.transformers.util.CleaningRegexes

object TextCleaner extends Function[WikiPage[_], WikiPage[Altered]] {
  import CleaningRegexes._

  def apply(wikiPage: WikiPage[_]): WikiPage[Altered] = {
    def loop(text: String, regex: String): String = {
      val res = text.replaceAll(regex, "")
      if(res == text)
        text
      else loop(res, regex)
    }
    val afterRecursive = recursiveRegexes.fold(wikiPage.revision.text)((text, regex) => loop(text, regex))
    val text = singleUseRegexes.fold(afterRecursive)((text, regex) => text.replaceAll(regex, ""))
    wikiPage.copy(revision = wikiPage.revision.copy(text = text))
  }
}

