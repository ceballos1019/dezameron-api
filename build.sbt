name := """dezameron-api"""
organization := "co.edu.udea"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5"
)

//libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "co.edu.udea.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "co.edu.udea.binders._"
