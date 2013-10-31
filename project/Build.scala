
import sbt._
import Keys._

object DemoBuild extends Build {
  if (System.getenv("HIVE_HOME") == null) {
    System.err.println("You must set HIVE_HOME to compile this project")
    System.exit(1)
  }

  // Shark version
  val SHARK_VERSION = "0.8.0"

  val SPARK_VERSION = "0.8.0-incubating"

  val SCALA_VERSION = "2.9.3"

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = coreSettings)

  val excludeNetty = ExclusionRule(organization = "org.jboss.netty")
  val excludeAsm = ExclusionRule(organization = "asm")

  def coreSettings = Defaults.defaultSettings ++ Seq(

    name := "SharkExample",
    organization := "com.databricks",
    version := "0.1",
    scalaVersion := SCALA_VERSION,
    scalacOptions := Seq("-deprecation", "-unchecked", "-optimize"),

    // Download managed jars into lib_managed.
    retrieveManaged := true,
    resolvers ++= Seq(
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
      "Spray Repository" at "http://repo.spray.io/",
      "Cloudera Repository" at "http://repository.cloudera.com/artifactory/cloudera-repos/",
      "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"
    ),

    fork := true,
    javaOptions += "-XX:MaxPermSize=512m",
    javaOptions += "-Xmx3g",

    unmanagedJars in Compile <++= baseDirectory map { base =>
      val hiveFile = file(System.getenv("HIVE_HOME")) / "lib"
      val baseDirectories = (base / "lib") +++ (hiveFile)
      val customJars = (baseDirectories ** "*.jar")
      // Hive uses an old version of guava that doesn't have what we want.
      customJars.classpath.filter(!_.toString.contains("guava"))
    },

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % SPARK_VERSION,
      "edu.berkeley.cs.amplab" %% "shark" % SHARK_VERSION
    ),
    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)
  )
}
