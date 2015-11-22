name := "smpp-streams"
organization := "org.bulatnig"
version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += Resolver.mavenLocal

// libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % "2.0-M1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
