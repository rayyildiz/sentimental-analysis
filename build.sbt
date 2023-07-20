scalaVersion := "2.12.17"

lazy val sentimentalAnalysis = (project in file("."))
  .settings(Settings.settings: _*)
  .settings(Settings.publishSettings: _*)
  .settings(Settings.dockerSettings: _*)
  .settings(Settings.testSettings: _*)
  .settings(Settings.sentimentalSettings: _*)
  .settings(libraryDependencies ++= Dependencies.akkaDependencies)
  .settings(libraryDependencies ++= Dependencies.googleDependencies)
  .settings(libraryDependencies ++= Dependencies.utilDependencies)
  .settings(libraryDependencies ++= Dependencies.testDependencies)
  .enablePlugins(DockerPlugin)
