package com.scalableminds.wikistreamer.parser

import javax.xml.stream.XMLInputFactory
import org.codehaus.staxmate.SMInputFactory
import org.codehaus.staxmate.in.SMInputCursor
import java.io._

object WikiXmlPullParser {

  val NormalPageNameSpace = "0"

  val CategoryPageNameSpace = "14"

  val allowedNamespaces = List(NormalPageNameSpace, CategoryPageNameSpace)

  private def parseContributor(contributorCursorData: SMInputCursor): Option[Contributor] = {
    def buildContributor(contributorBuilder: ContributorBuilder): Option[Contributor] = {
      if (contributorCursorData.getNext != null) {
        contributorCursorData.getLocalName match {
          case "id" => buildContributor(contributorBuilder.addId(contributorCursorData.collectDescendantText(false)))
          case "username" => buildContributor(contributorBuilder.addUserName(contributorCursorData.collectDescendantText(false)))
          case "ip" => buildContributor(contributorBuilder.addIp(contributorCursorData.collectDescendantText(false)))
          case _ => buildContributor(contributorBuilder)
        }
      } else {
        contributorBuilder.build
      }
    }

    buildContributor(ContributorBuilder())
  }

  private def parseRevision(revisionCursorData: SMInputCursor): Option[WikiPageRevision] = {

    def buildRevision(revisionBuilder: WikiPageRevisionBuilder): Option[WikiPageRevision] = {
      if(revisionCursorData.getNext != null) {
        revisionCursorData.getLocalName match {
          case "id" => buildRevision(revisionBuilder.addId(revisionCursorData.collectDescendantText(false)))
          case "parentid" => buildRevision(revisionBuilder.addParentId(revisionCursorData.collectDescendantText(false)))
          case "timestamp" => buildRevision(revisionBuilder.addTimeStamp(revisionCursorData.collectDescendantText(false)))
          case "contributor" =>
            val contributor = parseContributor(revisionCursorData.childCursor())
            contributor match {
              case Some(contributor) => buildRevision(revisionBuilder.addContributor(contributor))
              case None => None
            }
          case "comment" => buildRevision(revisionBuilder.addComment(revisionCursorData.collectDescendantText(false)))
          case "model" => buildRevision(revisionBuilder.addModel(revisionCursorData.collectDescendantText(false)))
          case "format" => buildRevision(revisionBuilder.addFormat(revisionCursorData.collectDescendantText(false)))
          case "text" => buildRevision(revisionBuilder.addText(revisionCursorData.collectDescendantText(false)))
          case "sha1" => buildRevision(revisionBuilder.addSha1(revisionCursorData.collectDescendantText(false)))
          case _ => buildRevision(revisionBuilder)
        }
      } else {
        revisionBuilder.build
      }
    }
    buildRevision(WikiPageRevisionBuilder())

  }

  private def parsePage(pageCursorData: SMInputCursor): Option[WikiPage] = {

    def buildPage(pageBuilder: WikiPageBuilder): Option[WikiPage] = {
      if(pageCursorData.getNext != null) {
        pageCursorData.getLocalName match {
          case "title" =>
            buildPage(pageBuilder.addTitle(pageCursorData.collectDescendantText(false)))
          case "id" =>
            buildPage(pageBuilder.addId(pageCursorData.collectDescendantText(false)))
          case "revision" =>
            val revision = parseRevision(pageCursorData.childCursor())
            revision match {
              case Some(revision) => buildPage(pageBuilder.addRevision(revision))
              case None => None
            }
          case "ns" =>
            buildPage(pageBuilder.addNs(pageCursorData.collectDescendantText(false)))
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