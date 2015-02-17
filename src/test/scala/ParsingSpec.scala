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
      parsed.head.text must not be empty

    }
  }
}

trait withWikiFile extends Scope {
  val wikiFilePath = "assets/dev-wiki.xml"
  val wikiFile = new File(wikiFilePath)
  val parsed = WikiXmlPullParser.parse(wikiFile)
}
