name := "subsriptions-manager"

version := "1.0"

scalaVersion := "2.12.3"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.17.0",
  "org.json4s" %% "json4s-native" % "3.5.2"
)