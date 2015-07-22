package com.scalableminds.wikistreamer.parser

import scala.collection.immutable.ListMap

//todo: make timestamp legit date
case class WikiPageRevision(id: String,
                            parentId: String,
                            timeStamp: String,
                            contributor: Contributor,
                            comment: Option[String],
                            model: String,
                            format: String,
                            text: String,
                            sha1: String,
                            sections: Option[ListMap[String, String]] = None,
                            categories: Option[Set[String]] = None,
                            links: Option[Array[String]] = None)