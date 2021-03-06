package com.scalableminds.wikistreamer.parser

import javax.xml.stream.XMLInputFactory
import org.codehaus.staxmate.SMInputFactory
import org.codehaus.staxmate.in.SMInputCursor
import java.io._
import scalaz.EphemeralStream


class WikiXmlPullParser {

  val NormalPageNameSpace = "0"

  val allowedNamespaces = List(NormalPageNameSpace)

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
            val contributorOpt = parseContributor(revisionCursorData.childCursor())
            contributorOpt match {
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
    buildRevision(new WikiPageRevisionBuilder)
  }

  private def parsePage(pageCursorData: SMInputCursor): Option[WikiPage[Original]] = {

    def buildPage(pageBuilder: WikiPageBuilder): Option[WikiPage[Original]] = {
      if(pageCursorData.getNext != null) {
        pageCursorData.getLocalName match {
          case "title" =>
            buildPage(pageBuilder.addTitle(pageCursorData.collectDescendantText(false)))
          case "id" =>
            buildPage(pageBuilder.addId(pageCursorData.collectDescendantText  (false)))
          case "revision" =>
            val revision = parseRevision(pageCursorData.childCursor())
            revision match {
              case Some(revision) => buildPage(pageBuilder.addRevision(revision))
              case None => None
            }
          case "ns" =>
            val ns = pageCursorData.collectDescendantText(false)
            if(allowedNamespaces.contains(ns))
              buildPage(pageBuilder.addNs(ns))
            else None
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

  def streamToIterator[T](s: EphemeralStream[T]) = {
    new Iterator[T] {
      var underlying = s

      override def hasNext: Boolean = !underlying.isEmpty

      override def next(): T = {
        if(underlying.isEmpty)
          throw new Exception("Empty Iterator")
        else {
          val h = underlying.head()
          underlying = underlying.tail()
          h
        }
      }
    }
  }

  private def parsePages(pagesCursor: SMInputCursor): EphemeralStream[WikiPage[Original]] = {
    if(pagesCursor.getNext != null){
      parsePage(pagesCursor.childElementCursor()) match{
        case Some(page) =>
          EphemeralStream.cons(page, parsePages(pagesCursor))
        case _ =>
          parsePages(pagesCursor)
      }
    } else
      EphemeralStream.emptyEphemeralStream[WikiPage[Original]]
  }

  def parseToIterator(fileName: String): Iterator[WikiPage[Original]] = {
    streamToIterator(parse(fileName))
  }

  def parseToIterator(file: File): Iterator[WikiPage[Original]] = {
    streamToIterator(parse(file))
  }

  def parse(fileName: String): EphemeralStream[WikiPage[Original]] = {
    parse(new File(fileName))
  }

  def parse(file: File): EphemeralStream[WikiPage[Original]] = {
    val factory = XMLInputFactory.newInstance()
    factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false)
    val inf = new SMInputFactory(factory)
    val rootCursor = inf.rootElementCursor(file).advance()
    val pageCursorData = rootCursor.childElementCursor("page")
    val stream = parsePages(pageCursorData) ++ {
      pageCursorData.getStreamReader.closeCompletely()
      EphemeralStream.emptyEphemeralStream[WikiPage[Original]]
    }
    stream
  }

  def parseAndStream(file: File) = {
    parse(file)
  }

}