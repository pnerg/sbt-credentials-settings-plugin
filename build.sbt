name := "sbt-credentials-settings-plugin"
organization := "org.dmonix.sbt"
version := "1.0.0"

sbtPlugin := true

scalaVersion := "2.12.6"
crossSbtVersions := Seq("1.2.8")

scalacOptions := Seq("-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-unchecked",
  "-deprecation",
  "-encoding", "utf8")

val `specs-core-version` = "4.3.4"
libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % `specs-core-version` % "test",
  "org.specs2" %% "specs2-mock" % `specs-core-version` % "test",
  "org.specs2" %% "specs2-junit" % `specs-core-version` % "test",
  "org.specs2" %% "specs2-matcher-extra" % `specs-core-version` % "test"
)