
name := "subscriptions-storage"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.3.0",
  "com.softwaremill.macwire" %% "proxy" % "2.3.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)