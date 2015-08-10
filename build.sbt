name := """wikistreamer"""

version := "1.0"

scalaVersion := "2.11.6"

// Change this to another test framework if you prefer

resolvers +=  "University Leipzig, AKSW Maven2 Repository" at "http://maven.aksw.org/repository/internal"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.specs2" %% "specs2-core" % "2.4.15" % "test"

libraryDependencies += "com.fasterxml.staxmate" % "staxmate" % "2.2.1"

libraryDependencies += "org.sweble.wikitext" % "swc-parser-lazy" % "2.0.0"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.3"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

