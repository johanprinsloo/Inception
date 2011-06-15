import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {

  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1"

  val logback = "ch.qos.logback" % "logback-classic" % "0.9.28"
  val grizzled = "org.clapper" % "grizzled-slf4j_2.9.0" % "0.5"

}