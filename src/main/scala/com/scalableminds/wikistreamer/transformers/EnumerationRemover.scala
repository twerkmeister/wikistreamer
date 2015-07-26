package com.scalableminds.wikistreamer.transformers

import com.scalableminds.wikistreamer.parser.{WikiPage, Altered, WikiPageRevision}

object EnumerationRemover extends Function[WikiPage[_], WikiPage[Altered]] {
  def apply(wikiPage: WikiPage[_]): WikiPage[Altered] = {
    val enumerationStarterLine = """(?m)^.*?:\n(?=^[\*#])"""
    val enumeration = """(?m)^:?[\*#].*?$"""
    val text = wikiPage.revision.text.replaceAll(enumerationStarterLine, "").replaceAll(enumeration, "")
    wikiPage.copy(revision = wikiPage.revision.copy(text = text))
  }
}
