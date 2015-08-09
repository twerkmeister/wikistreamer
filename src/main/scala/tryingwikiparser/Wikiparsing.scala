package tryingwikiparser

import com.scalableminds.wikistreamer.transformers.extractors.CategoryExtractor
import org.sweble.wikitext.parser._
import org.sweble.wikitext.parser.nodes.WtLinkTitle.WtNoLinkTitle
import org.sweble.wikitext.parser.nodes._
import org.sweble.wikitext.parser.utils.SimpleParserConfig
import com.scalableminds.wikistreamer.parser._
import java.io.File
import com.scalableminds.wikistreamer.transformers.TextCleaner
import com.scalableminds.wikistreamer.util.Pipeline._

import scala.util.Random


object Wikiparsing {
  def traverse(node: WtNode, annotation: String = ""): Unit = {
     node match {
      case text: WtText => print(annotation + text.getContent + annotation)
      case newLine: WtNewline => println(newLine.getContent)
      case internalLink: WtInternalLink =>
        val nextNode = if(internalLink.hasTitle) {
          internalLink.getTitle
        } else {
          internalLink.getTarget
        }
        val target = internalLink.getTarget.get(0).asInstanceOf[WtText]
        if(! (target.getContent.startsWith("Datei:") || target.getContent.startsWith("Kategorie:")))
          traverse(nextNode, "|")
      case section: WtSection => traverse(section.getBody, annotation)
      case listItem: WtListItem =>
      case xmlStartTag: WtNamedXmlElement =>
        xmlStartTag.getName match {
          case "ref" =>
          case "math" =>
          case _ =>
            xmlStartTag.toArray.foreach {
              case subnode: WtNode =>
                traverse(subnode, annotation)
            }
        }
      case x: WtNode =>
         x.toArray.foreach {
           case subnode: WtNode =>
             traverse(subnode, annotation)
           case a =>
             Console.err.println(a.toString)
         }
    }
  }

  def main(args: Array[String]) = {
    val wikiFileName = "assets/dev-wiki-long.xml"
    val wikiFile = new File(wikiFileName)

    val parser = new WikiXmlPullParser()
    val wikipages = parser.parse(wikiFile) |> CategoryExtractor |> TextCleaner

    val config = new SimpleParserConfig(true, true, true)
    val textParser = new WikitextParser(config)

    wikipages.filter(page => page.revision.categories.getOrElse(Set()).contains("Mann")) map { page =>

      val parsed = textParser.parseArticle(page.revision.text, page.title)
//      parsed.toArray().foreach{ node =>
//        println(node.toString)
//      }
      traverse(parsed)
    }
  }
}
