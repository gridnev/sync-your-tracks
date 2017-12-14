name := "event-hub"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7",
)
        