name := """play-scala-slick-postgres-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

resolvers += "Atlassian 3rd-Party" at "https://maven.atlassian.com/3rdparty/"

libraryDependencies += guice
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "4.11"
libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.16"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.1"
libraryDependencies += "com.typesafe.play" %% "play-slick" %  "3.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.2"
libraryDependencies += "com.github.tminglei" %% "slick-pg" % "0.15.4"
libraryDependencies += "com.github.tminglei" %% "slick-pg_play-json" % "0.15.4"

libraryDependencies += "postgresql" % "postgresql" % "9.4.1208-jdbc42-atlassian-hosted" //"9.1-901-1.jdbc4"

libraryDependencies += specs2 % Test
  

