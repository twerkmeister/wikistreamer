package com.scalableminds.wikistreamer.transformers.util

object SectionRegexes {
  val sectionRegex =
    """(?ms)^=+ (.*?) =+(.*?)(?=^=|\z)""".r
  val firstSectionRegex =
    """(?ms)(.*?)^=""".r
}
