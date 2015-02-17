package com.scalableminds.wikistreamer.parser


case class WikiPage(
                     id: String,
                     title: String,
                     text: String
                   ) {

  override def toString = {
    s"WikiPage($id, $title, text = '...')"
  }
}

