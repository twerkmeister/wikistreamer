package com.scalableminds.wikistreamer.parser

sealed trait WikiPageStatus
sealed trait Original extends WikiPageStatus
sealed trait Altered extends WikiPageStatus

case class WikiPage[WikiPageStatus](
                     title: String,
                     ns: String,
                     id: String,
                     revision: WikiPageRevision
                   ) {

  override def toString = {
    s"WikiPage($id, $title, text = '...')"
  }
}