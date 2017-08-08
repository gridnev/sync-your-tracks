name := "subsriptions-manager"

version := "1.0"

scalaVersion := "2.12.3"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7",
  "net.cakesolutions" %% "scala-kafka-client" % "0.11.0.0",
  "net.cakesolutions" %% "scala-kafka-client-akka" % "0.11.0.0"
)