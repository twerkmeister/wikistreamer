package com.scalableminds.wikistreamer.transformers.extractors

import com.scalableminds.wikistreamer.parser.{WikiPage, Original, WikiPageRevision}
import com.scalableminds.wikistreamer.transformers.util.SectionRegexes

import scala.collection.immutable.ListMap

object SectionExtractor extends Function[WikiPage[Original], WikiPage[Original]]{
  import SectionRegexes._

  def apply(wikiPage: WikiPage[Original]): WikiPage[Original] = {
    val sections = ListMap(firstSectionRegex.findFirstMatchIn(wikiPage.revision.text).map{m =>
      Seq("" -> m.group(1))
    }.getOrElse(Seq()) ++
      sectionRegex.findAllMatchIn(wikiPage.revision.text).map{m =>
        m.group(1) -> m.group(2)
      }:_*)
    wikiPage.copy(revision = wikiPage.revision.copy(sections = Some(sections)))
  }
}
