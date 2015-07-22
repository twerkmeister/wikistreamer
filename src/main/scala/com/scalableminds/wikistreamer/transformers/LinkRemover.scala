package com.scalableminds.wikistreamer.transformers

import com.scalableminds.wikistreamer.parser.{WikiPage, Altered, WikiPageRevision}
import com.scalableminds.wikistreamer.transformers.util.LinkRegexes

object LinkRemover {
  import LinkRegexes._

  def processText(wikiPage: WikiPage[_]): WikiPage[Altered] = {
    val text = wikiPage.revision.text.replaceAll(namedLinkRegex.toString, "$2").replaceAll(linkRegex.toString, "$1")
    wikiPage.copy(revision = wikiPage.revision.copy(text = text))
  }
}
