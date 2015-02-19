package com.scalableminds.wikistreamer.parser

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
//todo: make timestamp legit date
case class WikiPageRevision(id: String,
                            parentId: String,
                            timeStamp: String,
                            contributor: Contributor,
                            comment: Option[String],
                            model: String,
                            format: String,
                            text: String,
                            sha1: String)

sealed trait Contributor
case class User(username: String, id: String) extends Contributor
case class AnonymousUser(ip: String) extends Contributor