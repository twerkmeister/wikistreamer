package com.scalableminds.wikistreamer.transformers.extractors

import com.scalableminds.wikistreamer.parser.{WikiPage, Original, WikiPageRevision}

object CategoryExtractor extends Function[WikiPage[Original], WikiPage[Original]] {
  val categoriesRegex = """\[\[Kategorie:(.*?)(\|(.*?))?\]\]""".r

  def apply (wikiPage: WikiPage[Original]): WikiPage[Original] = {
    val extractedCategories = categoriesRegex.findAllMatchIn(wikiPage.revision.text).map{ categoryMatch =>
      categoryMatch.group(1)
    }.toSet
    wikiPage.copy(revision = wikiPage.revision.copy(categories = Some(extractedCategories)))
  }
}