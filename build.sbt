import AssemblyKeys._

name := "lucene-mlm"

version := "0.1"

organization := "ru.ksu.niimm.cll"

scalaVersion := "2.9.2"

resolvers += "Concurrent Maven Repo" at "http://conjars.org/repo"

resolvers += "North 52" at "http://52north.org/maven/repo/releases"

resolvers += "CLL Repo" at "http://cll.niimm.ksu.ru:8080/artifactory/ext-releases-local"

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)


libraryDependencies += "org.scala-tools.testing" % "specs_2.9.2" % "1.6.9" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

libraryDependencies += "org.apache.lucene" % "lucene-core" % "4.0.0"

libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "4.0.0"

libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "4.0.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9"

net.virtualvoid.sbt.graph.Plugin.graphSettings

parallelExecution in Test := false

seq(assemblySettings: _*)

// Some of these files have duplicates, let's ignore:
mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case s if s.endsWith(".class") => MergeStrategy.last
    case s if s.endsWith("project.clj") => MergeStrategy.concat
    case s if s.endsWith(".html") => MergeStrategy.last
    case s if s.endsWith("mailcap") => MergeStrategy.last
    case x => old(x)
  }
}
