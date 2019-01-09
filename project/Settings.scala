import sbt.Keys._
import sbt.{Def, _}
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.{assemblyMergeStrategy, MergeStrategy}
import sbtassembly.PathList
import sbtdocker.DockerPlugin.autoImport._
import sbtdocker.Dockerfile

object Settings {
  lazy val settings: Seq[Def.Setting[_]] = Seq(
    organization := "com.rayyildiz",
    version := "1.1." + sys.props.getOrElse("buildNumber", default = "0-SNAPSHOT"),
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.12.0", "2.12.1", "2.12.2", "2.12.3"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", _*)   => MergeStrategy.discard
      case PathList("reference.conf") => MergeStrategy.concat
      case _                          => MergeStrategy.first
    }
  )

  lazy val publishSettings: Seq[Def.Setting[_]] = Seq(
    publishTo := Some(
      if (isSnapshot.value) {
        Opts.resolver.sonatypeSnapshots
      } else {
        Opts.resolver.sonatypeStaging
      }
    ),
    publishArtifact in Test := false,
    publishMavenStyle := true,
    licenses := Seq("MIT" -> url("https://github.com/rayyildiz/sentimental-analysis/blob/master/LICENSE")),
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

  lazy val dockerSettings: Seq[Def.Setting[_]] = Seq(
    dockerfile in docker := {
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"

      new Dockerfile {
        from("rayyildiz/java8:jre")
        add(artifact, artifactTargetPath, chown = "daemon:daemon")
        label("maintainer", "rayyildiz")
        expose(8080)
        entryPoint("java", "-jar", artifactTargetPath)
      }
    }
  )

  lazy val testSettings: Seq[Def.Setting[_]] = Seq(
    fork in Test := false,
    parallelExecution in Test := false,
    test in assembly := {}
  )

  lazy val sentimentalSettings: Seq[Def.Setting[_]] = Seq(
    name := "sentimental-analysis"
  )
}
