import sbt._
import Keys._
import trafficland.opensource.sbt.plugins._

object SBTPluginsBuild extends Build {

  val pluginVersion = "0.13.0-SNAPSHOT".toReleaseFormat()

  lazy val root = Project(id = "sbt-plugins", base = file("."),
    settings = StandardPluginSet.plugs ++ LibraryDependencies.playPlugin)
    .settings(
      isApp := false,
      name := "sbt-plugins",
      organization := "com.trafficland",
      organizationName := "TrafficLand, Inc.",
      sbtPlugin := true,
      version       := pluginVersion,
      scalaVersion := "2.10.3",
      scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
      resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      libraryDependencies   ++= LibraryDependencies.toSeq,
      publishTo <<= (version) { version: String =>
        val scalasbt = "http://repo.scala-sbt.org/scalasbt/"
        val (name, url) = version.isSnapshot match {
          case true => ("community-sbt-plugin-snapshots", scalasbt+"sbt-plugin-snapshots")
          case false => ("community-sbt-plugin-releases", scalasbt+"sbt-plugin-releases")
        }
        Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
      },
      publishMavenStyle := false,
      credentials += Credentials(Path.userHome / ".ivy2" / "tlcredentials" / ".scala-sbt-credentials"),
      publish <<= (publish, version, streams) map { (result, appVersion, stream) =>
        appVersion.isSnapshot match {
          case false => writeReadme(appVersion, stream)
          case true => stream.log.info("This is a snapshot.  The README file does not need to be altered.")
        }
        result
      }
    )

  def writeReadme(version:String, stream:TaskStreams) {
    stream.log.info("Starting to write README.md.")
    val readmeTemplate = file("./docs/README.md.template")
    val readmeFile = file("./README.md")
    IO.write(readmeFile, IO.read(readmeTemplate).replace("#{VERSION}", version))
    stream.log.info("Finished writing README.md.")
  }
}
