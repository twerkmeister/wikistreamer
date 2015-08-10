package com.scalableminds.wikistreamer.transformers.wikitextparser

import com.scalableminds.wikistreamer.parser.{Altered, WikiPage}
import org.slf4j.LoggerFactory
import org.sweble.wikitext.parser.WikitextParser
import org.sweble.wikitext.parser.nodes._
import org.sweble.wikitext.parser.utils.SimpleParserConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WikiTextParser extends Function3[Set[String], CategoryResolver, WikiPage[_], Future[WikiPage[Altered]]] {
  val config = new SimpleParserConfig(true, true, true)
  val textParser = new WikitextParser(config)

  val logger = LoggerFactory.getLogger(this.getClass.getName)


  def traverse(neededCategories: Set[String], categoryResolver: CategoryResolver)(node: WtNode): Future[String] = {
    def loop(node: WtNode, annotation: String = ""): Future[String] = {
      node match {
        case text: WtText => Future.successful(annotation + text.getContent + annotation)
        case newLine: WtNewline => Future.successful(newLine.getContent)
        case internalLink: WtInternalLink =>
          val target = internalLink.getTarget.get(0).asInstanceOf[WtText].getContent
          categoryResolver.categoriesForPage(target).flatMap{categories =>
            logger.info(s"$target: ${categories.mkString(",")}")
            val nextNode = if (internalLink.hasTitle) {
              internalLink.getTitle
            } else {
              internalLink.getTarget
            }

            if (!target.startsWith("Kategorie:")) {
              if (categories.intersect(neededCategories).nonEmpty)
                loop(nextNode, "|")
              else
                loop(nextNode)
            }
            else
              Future.successful("")
          }

        case section: WtSection => loop(section.getBody, annotation)
        case listItem: WtListItem => Future.successful("")
        case xmlStartTag: WtNamedXmlElement =>
          xmlStartTag.getName match {
            case "ref" => Future.successful("")
            case "math" => Future.successful("")
            case _ =>
              Future.traverse(xmlStartTag.toArray.toSeq){
                case subnode: WtNode =>
                  loop(subnode, annotation)
              }.map{_.mkString("")}
          }
        case x: WtNode =>
          Future.traverse(x.toArray.toSeq){
            case subnode: WtNode =>
              loop(subnode, annotation)
            case a =>
              Console.err.println(a.toString)
              Future.successful("")
          }.map{_.mkString("")}
      }
    }
    loop(node, "")
  }

  def apply(neededCategories: Set[String], categoryResolver: CategoryResolver, wikiPage: WikiPage[_]): Future[WikiPage[Altered]] = {
    val parsed = textParser.parseArticle(wikiPage.revision.text, wikiPage.title)
    val textFuture = traverse(neededCategories, categoryResolver)(parsed)
    textFuture.map{ text =>
      wikiPage.copy(revision = wikiPage.revision.copy(text = text))
    }
  }
}
