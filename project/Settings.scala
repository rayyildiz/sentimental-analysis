import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.{MergeStrategy, assemblyMergeStrategy}
import sbtassembly.PathList

object Settings {
  lazy val settings = Seq(
    organization := "com.rayyildiz",
    version := "1.0." + sys.props.getOrElse("buildNumber", default = "0-SNAPSHOT"),
    scalaVersion := "2.12.3",
    crossScalaVersions := Seq("2.12.0", "2.12.1", "2.12.2"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_ *) => MergeStrategy.discard
      case PathList("reference.conf") => MergeStrategy.concat
      case x => MergeStrategy.first
    }
  )

  lazy val publishSettings = Seq(
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    publishArtifact in Test := false,
    publishMavenStyle := true,
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage := Some(url("https://github.com/rayyildiz/sentimental-analysis")),
    scmInfo := Some(
      ScmInfo(
        url("http://github.com/rayyildiz/sentimental-analysis"),
        "scm:git:git@github.com:rayyildiz/sentimental-analysis.git"
      )
    ),
    developers := List(
      Developer(id = "rayyildiz", name = "Ramazan AYYILDIZ", email = "", url = url("http://rayyildiz.com"))
    )
  )

  lazy val testSettings = Seq(
    fork in Test := false,
    parallelExecution in Test := false,
    test in assembly := {}
  )

  lazy val sentimentalSettings = Seq(
    name := "sentimental-analysis"
  )
}