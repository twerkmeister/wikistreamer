package com.scalableminds.wikistreamer.parser


sealed trait Contributor
case class User(username: String, id: String) extends Contributor
case class AnonymousUser(ip: String) extends Contributor