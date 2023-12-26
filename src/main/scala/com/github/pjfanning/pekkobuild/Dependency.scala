package com.github.pjfanning.pekkobuild

import sbt.VersionNumber

sealed trait Dependency {
  def version: String
  // The version to use in api/japi/docs links,
  // so 'x.y', 'x.y.z', 'current' or 'snapshot'
  def link: String
}

case class Artifact(version: String, isSnapshot: Boolean) extends Dependency {
  override def link: String =
    VersionNumber(version) match { case VersionNumber(Seq(x, y, _*), _, _) => s"$x.$y" }
}

object Artifact {
  def apply(version: String): Artifact = {
    val isSnap = version.endsWith("-SNAPSHOT")
    new Artifact(version, isSnap)
  }
}
case class Sources(uri: String, link: String = "current") extends Dependency {
  def version: String = link
}
