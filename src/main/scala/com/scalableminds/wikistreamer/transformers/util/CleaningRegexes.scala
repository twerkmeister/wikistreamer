package com.scalableminds.wikistreamer.transformers.util

object CleaningRegexes {
  val doubleCurly = """\{\{[^\{]*?\}\};?"""
  val singleCurly = """:?\{[^\{]*?\};?"""
  val recursiveRegexes = List(doubleCurly, singleCurly)

  val refs = """<ref.*?>.*?<(/ref.*?|ref.*?/)>"""
  val source = """(?s)<(source.*?|code.*?)>.*?<(/source.*?|source.*?/|/code.*?|code.*?/)>"""
  val amp = """&nbsp;"""
  val enclosingAngledbrackets = """<(.*?)( .*?)?>(.*?)</\1>"""
  val galleries = """(?s)<gallery([^<>]*?)?>.*?</gallery>"""
  val angledBrackets = """<.*?>"""
  val typedSquareBrackets ="""\[\[([^\[\] ]*?):[^\[\]]*?(\[\[[^\[\]]*?\]\])?[^\[\]]*?\]\]"""
  val htmlComment = """(?s)<!--.*?-->"""
  val outlinks = """\[https?://[^\[\]]*?\]"""
  val emptyLines = """(?m)^(:|\*|;)\s*$"""
  val files = """(?m)^\[\[Datei:.*?$"""


//  val singleUseRegexes = List(refs, enclosingAngledbrackets, angledBrackets, amp, galleries, typedSquareBrackets, htmlComment, emptyLines)
  val singleUseRegexes = List(refs, files, amp, source, galleries, enclosingAngledbrackets, htmlComment)
}
