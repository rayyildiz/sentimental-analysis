import sbt.Keys.*
import sbt.{Def, *}
import sbtassembly.AssemblyKeys.*
import sbtassembly.AssemblyPlugin.autoImport.{MergeStrategy, assemblyMergeStrategy}
import sbtassembly.PathList
import sbtdocker.DockerPlugin.autoImport.*
import sbtdocker.Dockerfile

object Settings {
  lazy val settings: Seq[Def.Setting[?]] = Seq(
    organization := "com.rayyildiz",
    version := "1.1." + sys.props.getOrElse("buildNumber", default = "0-SNAPSHOT"),
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.12.0", "2.12.1", "2.12.2", "2.12.3"),
    publishMavenStyle := true,
    Test / publishArtifact := false,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*)   => MergeStrategy.discard
      case PathList("reference.conf") => MergeStrategy.concat
      case _                          => MergeStrategy.first
    }
  )

  lazy val publishSettings: Seq[Def.Setting[?]] = Seq(
    publishTo := Some(
      if (isSnapshot.value) {
        Opts.resolver.sonatypeOssSnapshots.head
      } else {
        Opts.resolver.sonatypeStaging
      }
    ),
    Test / publishArtifact := false,
    publishMavenStyle := true,
    licenses := Seq("MIT" -> url("https://github.com/rayyildiz/sentimental-analysis/blob/master/LICENSE")),
    homepage := Some(url("https://github.com/rayyildiz/sentimental-analysis")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/rayyildiz/sentimental-analysis"),
        "scm:git:git@github.com:rayyildiz/sentimental-analysis.git"
      )
    ),
    developers := List(
      Developer(id = "rayyildiz", name = "Ramazan AYYILDIZ", email = "", url = url("https://rayyildiz.com"))
    )
  )

  lazy val dockerSettings: Seq[Def.Setting[?]] = Seq(
    docker / dockerfile := {
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

  lazy val testSettings: Seq[Def.Setting[?]] = Seq(
    Test / fork := false,
    Test / parallelExecution := false,
    assembly / test := {}
  )

  lazy val sentimentalSettings: Seq[Def.Setting[?]] = Seq(
    name := "sentimental-analysis"
  )
}
