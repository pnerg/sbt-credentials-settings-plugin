name := "sbt-credentials-settings-plugin"
organization := "org.dmonix.sbt"
version := "1.0.0"

sbtPlugin := true

scalaVersion := "2.12.6"
crossScalaVersions := Seq("2.10.6", "2.12.6")
crossSbtVersions := Seq("0.13.17", "1.2.8")

scalacOptions := Seq("-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-unchecked",
  "-deprecation",
  "-encoding", "utf8")