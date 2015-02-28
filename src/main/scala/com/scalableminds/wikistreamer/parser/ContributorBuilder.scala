package com.scalableminds.wikistreamer.parser

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
