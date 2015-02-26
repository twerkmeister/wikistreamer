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

  def build = {
    (id, parentId, timeStamp, contributor, comment, model, format, text, sha1) match {
      case (Some(id), Some(parentId), Some(timeStamp), Some(contributor), commentOpt, Some(model), Some(format), Some(text), Some(sha1)) => Some(new WikiPageRevision(id, parentId, timeStamp, contributor, commentOpt, model, format, text, sha1) with RemoveEnumerations with RemoveLinks  with CleanedText with Categories)
      case _ => None
    }
  }
}

case class ContributorBuilder(id: Option[String] = None, userName: Option[String] = None, ip: Option[String] = None) {
  def addId(id: String) = copy(id = Some(id))
  def addUserName(userName: String) = copy(userName = Some(userName))
  def addIp(ip: String) = copy(ip = Some(ip))

  def build: Option[Contributor] = {
    ((id, userName) match {
      case (Some(id), Some(userName)) => Some(User(userName, id))
      case _ => None
    }) orElse(
    ip.map(ip =>  AnonymousUser(ip))
    )
  }
}

