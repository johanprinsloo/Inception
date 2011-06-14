import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {

  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1"

}