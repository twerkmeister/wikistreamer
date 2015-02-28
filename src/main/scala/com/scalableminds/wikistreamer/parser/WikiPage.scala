package com.scalableminds.wikistreamer.parser


import scala.collection.immutable.ListMap
import scala.util.matching.Regex

case class WikiPage(
                     title: String,
                     ns: String,
                     id: String,
                     revision: WikiPageRevision
                   ) {

  override def toString = {
    s"WikiPage($id, $title, text = '...')"
  }
}