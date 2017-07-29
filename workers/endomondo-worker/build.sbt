name := "endomondo-worker"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.17.0",
  "org.json4s" %% "json4s-native" % "3.5.2"
)
    