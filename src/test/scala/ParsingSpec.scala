import java.io.File

import org.specs2.mutable._
import org.specs2.specification.Scope
import com.scalableminds.wikistreamer.parser.WikiXmlPullParser

class ParsingSpec extends Specification {
  "The wiki parser" should {
    "parse 5 pages from the dev wiki" in new withWikiFile {
      parsed.length must be_==(5)
    }
    "parse the right content" in new withWikiFile {
      parsed.head.title must beEqualTo("Alan Smithee")
      parsed.head.revision.text must not be empty
    }
    "parse sections" in new withWikiFile {
      val firstPage = parsed.head
      firstPage.revision.sections.foreach{ case (headline, content) => println(headline)}
      firstPage.revision.sections.size must be_==(8)
      firstPage.revision.sections.get("Entstehung") must beSome[String]
      val secondPage = parsed.tail.head
      secondPage.revision.sections.foreach{ case (headline, content) => println(headline)}
      secondPage.revision.sections.size must be_==(16)
      parsed.foreach{page =>
        page.revision.sections.foreach{
          case (title, text) =>
            println(title)
            println("=" *title.size)
            println(text)
            println()
        }
      }
    }
  }
}

trait withWikiFile extends Scope {
  val wikiFilePath = "assets/dev-wiki.xml"
  val wikiFile = new File(wikiFilePath)
  val parsed = WikiXmlPullParser.parse(wikiFile)
}
