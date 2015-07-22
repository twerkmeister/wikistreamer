package com.scalableminds.wikistreamer.transformers.util

object LinkRegexes {
  val linkRegex = """\[\[(.*?)\]\]""".r
  val namedLinkRegex = """\[\[([^\[\]]*?)\|(.*?)\]\]""".r
}
