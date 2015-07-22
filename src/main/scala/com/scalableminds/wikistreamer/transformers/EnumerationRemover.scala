package com.scalableminds.wikistreamer.transformers

import com.scalableminds.wikistreamer.parser.{WikiPage, Altered, WikiPageRevision}

object EnumerationRemover {
  def processText(wikiPage: WikiPage[_]): WikiPage[Altered] = {
    val enumerationStarterLine = """(?m)^.*?:\n(?=^[\*#])"""
    val enumeration = """(?m)^:?[\*#].*?$"""
    val text = wikiPage.revision.text.replaceAll(enumerationStarterLine, "").replaceAll(enumeration, "")
    wikiPage.copy(revision = wikiPage.revision.copy(text = text))
  }
}
