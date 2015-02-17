package com.scalableminds.wikistreamer.parser

case class WikiPageBuilder(id: Option[String] = None, title: Option[String] = None, text: Option[String] = None) {

  def addTitle(title: String) =
    copy(title = Some(title))

  def addText(text: String) =
    copy(text = Some(text))

  def addId(id: String) =
    copy(id = Some(id))

  def build = {
    (id, title, text) match {
      case (Some(i), Some(ttl), Some(txt)) => Some(WikiPage(i, ttl, txt))
      case _ => None
    }
  }
}