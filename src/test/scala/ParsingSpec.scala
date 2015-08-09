import java.io.File

import com.scalableminds.wikistreamer.transformers.extractors.{LinkExtractor, SectionExtractor}
import org.specs2.mutable._
import org.specs2.specification.Scope
import com.scalableminds.wikistreamer.parser.WikiXmlPullParser
import com.scalableminds.wikistreamer.util.Pipeline._

import scala.collection.immutable.ListMap

class ParsingSpec extends Specification {
  "The wiki parser" should {
    "parse 5 pages from the dev wiki" in new withDevWiki {
      parsed.length must be_==(5)
    }

    "parse the right content" in new withDevWiki {
      parsed.headOption must beSome
      parsed.headOption foreach { firstPage =>
          firstPage.title must beEqualTo("Alan Smithee")
          firstPage.revision.text must not be empty
      }
    }

    "extract sections" in new withDevWiki {
      val pagesWithSections = parsed |> SectionExtractor
      pagesWithSections.headOption must beSome
      pagesWithSections.headOption.foreach { firstPage =>
        firstPage.revision.sections must beSome
        firstPage.revision.sections foreach { sections =>
          sections.size must be_==(8)
          sections.get("Entstehung") must beSome[String]
        }
      }
    }

    "extract links" in new withDevWiki {
      val pagesWithLinks = parsed |> LinkExtractor
      pagesWithLinks.headOption must beSome
      pagesWithLinks.headOption foreach { firstPage =>
        firstPage.revision.links must beSome
        firstPage.revision.links foreach { links =>
          println(links.mkString("\n"))
        }
      }
    }



//    "parse sections" in new withWikiFile {
//      val firstPage = parsed.head
//      firstPage.revision.sections.foreach{ case (headline, content) => println(headline)}
//      firstPage.revision.sections.size must be_==(8)
//      firstPage.revision.sections.get("Entstehung") must beSome[String]
//      val secondPage = parsed.tail.head
//      secondPage.revision.sections.foreach{ case (headline, content) => println(headline)}
//      secondPage.revision.sections.size must be_==(16)
//      parsed.foreach{page =>
//        page.revision.sections.foreach{
//          case (title, text) =>
//            println(title)
//            println("=" *title.size)
//            println(text)
//            println()
//        }
//      }
//    }

//    "parse categories" in new withWikiFile {
//      val firstPage = parsed.head
//      firstPage.revision.categories.size must be_==(4)
////      println(firstPage.revision.categories.mkString("\n"))
//    }
//
//    "extract Links" in new withWikiFile {
//      val firstPage = parsed.head
//      firstPage.revision.links.size must be_>(0)
//    }
  }
}

trait withWikiFile extends Scope {
  def wikiFilePath: String
  lazy val wikiFile = new File(wikiFilePath)
  lazy val parser = new WikiXmlPullParser
  lazy val parsed = parser.parse(wikiFile)
}

trait withDevWiki extends withWikiFile {
  val wikiFilePath = "assets/dev-wiki.xml"
}

trait withLongDevWiki extends withWikiFile {
  val wikiFilePath = "assets/dev-wiki-long.xml"
}