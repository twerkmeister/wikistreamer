package com.scalableminds.wikistreamer.transformers.wikitextparser

import scala.concurrent.Future

trait CategoryResolver {
  def categoriesForPage(title: String): Future[Set[String]]
}
