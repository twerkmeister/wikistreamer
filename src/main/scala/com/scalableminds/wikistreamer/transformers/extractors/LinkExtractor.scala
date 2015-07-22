package com.scalableminds.wikistreamer.transformers.extractors

import com.scalableminds.wikistreamer.parser.{WikiPage, Original, WikiPageRevision}
import com.scalableminds.wikistreamer.transformers.util.LinkRegexes

object LinkExtractor{
  import LinkRegexes._

  def extractLinks(wikiPage: WikiPage[Original]): WikiPage[Original] = {
    val links = (namedLinkRegex.findAllMatchIn(wikiPage.revision.text).map{m => m.group(2)} ++
      linkRegex.findAllMatchIn(wikiPage.revision.text).map{m => m.group(1)}).toArray
    wikiPage.copy(revision = wikiPage.revision.copy(links = Some(links)))
  }
}
