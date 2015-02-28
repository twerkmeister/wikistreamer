package com.scalableminds.wikistreamer.parser

case class WikiPageBuilder(title: Option[String] = None, ns: Option[String] = None, id: Option[String] = None,  revision: Option[WikiPageRevision] = None) {

  def addTitle(title: String) =
    copy(title = Some(title))

  def addNs(ns: String) =
    copy(ns = Some(ns))

  def addId(id: String) =
    copy(id = Some(id))

  def addRevision(revision: WikiPageRevision) =
  copy(revision = Some(revision))


  def build = {
    (title, ns, id, revision) match {
      case (Some(title), Some(ns), Some(id), Some(revision)) => Some(WikiPage(title, ns, id, revision))
      case _ => None
    }
  }
}

