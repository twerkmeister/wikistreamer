package com.scalableminds.wikistreamer.parser

case class WikiPageRevisionBuilder(id: Option[String] = None,
                                   parentId: Option[String] = None,
                                   timeStamp: Option[String] = None,
                                   contributor: Option[Contributor] = None,
                                   comment: Option[String] = None,
                                   model: Option[String] = None,
                                   format: Option[String] = None,
                                   text: Option[String] = None,
                                   sha1: Option[String] = None) {


  def addId(id: String) = copy(id = Some(id))

  def addParentId(parentId: String) = copy(parentId = Some(parentId))

  def addTimeStamp(timeStamp: String) = copy(timeStamp = Some(timeStamp))

  def addContributor(contributor: Contributor) = copy(contributor = Some(contributor))

  def addComment(comment: String) = copy(comment = Some(comment))

  def addModel(model: String) = copy(model = Some(model))

  def addFormat(format: String) = copy(format = Some(format))

  def addText(text: String) = copy(text = Some(text))

  def addSha1(sha1: String) = copy(sha1 = Some(sha1))

  def build: Option[WikiPageRevision] = {
    (id, parentId, timeStamp, contributor, comment, model, format, text, sha1) match {
      case (Some(id), Some(parentId), Some(timeStamp), Some(contributor), commentOpt, Some(model), Some(format), Some(text), Some(sha1)) =>
        Some(WikiPageRevision(id, parentId, timeStamp, contributor, comment, model, format, text, sha1))
      case _ => None
    }
  }

}