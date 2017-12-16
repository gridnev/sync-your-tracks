name := "strava-stub"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.9"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

maintainer in Docker := "dgridnev"

packageName in Docker := "dgridnev/strava-stab"