name := """wikistreamer"""

version := "1.0"

scalaVersion := "2.11.4"

// Change this to another test framework if you prefer

resolvers +=  "University Leipzig, AKSW Maven2 Repository" at "http://maven.aksw.org/repository/internal"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

//libraryDependencies += "com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0-M3"

libraryDependencies += "org.specs2" %% "specs2-core" % "2.4.15" % "test"

libraryDependencies += "com.fasterxml.staxmate" % "staxmate" % "2.2.1"

//libraryDependencies += "org.dbpedia.extraction" % "core" % "4.0"

libraryDependencies += "org.sweble.wikitext" % "swc-parser-lazy" % "2.0.0"


