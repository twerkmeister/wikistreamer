package com.scalableminds.wikistreamer.parser

import javax.xml.stream.XMLInputFactory
import org.codehaus.staxmate.SMInputFactory
import org.codehaus.staxmate.in.SMInputCursor
import java.io._

object WikiXmlPullParser {

  val NormalPageNameSpace = "0"

  val CategoryPageNameSpace = "14"

  val allowedNamespaces = List(NormalPageNameSpace, CategoryPageNameSpace)

  private def parsePage(pageCursorData: SMInputCursor): Option[WikiPage] = {
    def buildPage(pageBuilder: WikiPageBuilder): Option[WikiPage] = {
      if(pageCursorData.getNext != null) {
        pageCursorData.getLocalName match {
          case "title" =>
            buildPage(pageBuilder.addTitle(pageCursorData.collectDescendantText(false)))
          case "id" =>
            buildPage(pageBuilder.addId(pageCursorData.collectDescendantText(false)))
          case "revision" =>
            buildPage(pageBuilder.addText(pageCursorData.childElementCursor("text").advance().collectDescendantText(false)))
          case "ns" =>
            val ns = pageCursorData.collectDescendantText(false)
            if (!allowedNamespaces.contains(ns))
              None
            else
              buildPage(pageBuilder)
          case _ =>
            buildPage(pageBuilder)
        }
      } else
        pageBuilder.build
    }

    try {
      buildPage(new WikiPageBuilder)
    } catch {
      case e: Exception =>
        println("Catched exception.")
        e.printStackTrace()
        None
    }
  }

  def streamToIterator[T](s: Stream[T]) = {
    new Iterator[T] {
      var underlying = s

      override def hasNext: Boolean = ! underlying.isEmpty

      override def next(): T = {
        underlying match {
          case x #:: xs =>
            underlying = xs
            x
          case _ =>
            throw new Exception("Empty Iterator")
        }
      }
    }
  }

  private def parsePages(pagesCursor: SMInputCursor): Stream[WikiPage] = {
    if(pagesCursor.getNext != null){
      parsePage(pagesCursor.childElementCursor()) match{
        case Some(page) =>
          Stream.cons(page, parsePages(pagesCursor))
        case _ =>
          parsePages(pagesCursor)
      }
    } else
      Stream.empty
  }

  def parse(file: File): Stream[WikiPage] = {
    val factory = XMLInputFactory.newInstance()
    factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false)
    val inf = new SMInputFactory(factory)
    val rootCursor = inf.rootElementCursor(file).advance()
    println(rootCursor.getLocalName)
    val pageCursorData = rootCursor.childElementCursor("page")
    val stream = parsePages(pageCursorData) ++ {
      pageCursorData.getStreamReader.closeCompletely()
      Stream.empty[WikiPage]
    }

    stream
  }
}