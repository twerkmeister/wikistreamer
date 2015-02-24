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
//todo: make timestamp legit date
case class WikiPageRevision(id: String,
                            parentId: String,
                            timeStamp: String,
                            contributor: Contributor,
                            comment: Option[String],
                            model: String,
                            format: String,
                            text: String,
                            sha1: String) {

  private val sectionRegex =
    """(?ms)^=+ (.*?) =+(.*?)(?=^=|\z)""".r
  private val firstSectionRegex =
    """(?ms)(.*?)^=""".r
  lazy val sections: ListMap[String, String] = {
    ListMap(((firstSectionRegex.findFirstMatchIn(text).map{m =>
      Seq("" -> m.group(1))
    }).getOrElse(Seq()) ++
      sectionRegex.findAllMatchIn(text).map{m =>
      m.group(1) -> m.group(2)
    }):_*)
  }
}

sealed trait Contributor
case class User(username: String, id: String) extends Contributor
case class AnonymousUser(ip: String) extends Contributor