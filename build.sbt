name := "score_generator"
version := "0.1"
scalaVersion := "2.11.11"

unmanagedClasspath in(Compile, runMain) += baseDirectory.value / "conf" / "hadoop"

val sparkVersion = "2.2.0"
val akkaVersion = "2.5.4"
val akkaHttpVersion = "10.0.10"
lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.hadoop" % "hadoop-aws" % "2.9.2"
)

lazy val commonDependencies = Seq(
  "joda-time" % "joda-time" % "2.10",
  "log4j" % "log4j" % "1.2.17"
)

lazy val akkaDependencies: Seq[ModuleID] = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
)

val recon = project.in(new File("recon"))
  .settings(libraryDependencies ++= sparkDependencies.map(_ % "provided")
    ++ akkaDependencies
    ++ Seq("com.typesafe" % "config" % "1.2.1")
    ++ commonDependencies
    ++ Seq("org.antlr" % "antlr4-runtime" % "4.7.2")
  )

val apis = project.in(new File("apis"))
  .dependsOn(recon)
  .settings(libraryDependencies ++=
    sparkDependencies.map(_ % "provided")
      ++ akkaDependencies
      ++ commonDependencies
  )

lazy val root = (project in file("."))
  .aggregate(apis, recon)

lazy val runner = project.in(new File("runner"))
  .dependsOn(apis)
  .dependsOn(recon)
  .settings(libraryDependencies ++= sparkDependencies.map(_ % "compile") ++ akkaDependencies)

assemblyMergeStrategy in assembly := {
  case "io.netty.versions.properties" => MergeStrategy.concat
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case x => MergeStrategy.deduplicate
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter { f =>
    f.data.getName.contains("hadoop") ||
      f.data.getName.contains("spark") ||
      f.data.getName.contains("spark-core") ||
      f.data.getName.contains("spark-sql") ||
      f.data.getName.contains("javax.inject-1")
  }
}

resolvers ++= Seq(
  "Spray repository" at "http://repo.spray.io",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

resolvers ++= Seq(
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

javaOptions in run ++= Seq(
  //  "-Dlog4j.debug=true",
  "-Dlog4j.configuration=log4j.properties")


// set the main class for packaging the main jar
mainClass in(Compile, packageBin) := Some("Main")

// set the main class for the main 'sbt run' task
mainClass in(Compile, run) := Some("Main")