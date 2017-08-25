name := "sentimental-analysis"
organization := "com.rayyildiz"
version := "1.0"

scalaVersion := "2.12.3"

val akkaV = "2.4.19"
val akkaHttpV = "10.0.9"

crossScalaVersions := Seq("2.12.0", "2.12.1", "2.12.2")

libraryDependencies ++= Seq(
  // Common Libraries
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  // Libraries
  "org.jsoup" % "jsoup" % "1.10.3",
  "com.google.cloud" % "google-cloud-translate" % "0.20.2-beta",
  "com.google.cloud" % "google-cloud-language" % "0.20.2-beta",
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  // Akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
  // Test
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_ *) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}

test in assembly := {}
mainClass in assembly := Some("com.rayyildiz.sentiment_analyzer.BootApplication")
