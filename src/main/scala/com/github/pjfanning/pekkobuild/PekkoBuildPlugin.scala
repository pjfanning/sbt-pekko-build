package com.github.pjfanning.pekkobuild

import com.github.pjfanning.pekkobuild.PekkoBuildPlugin.autoImport.pekkoMinVersion
import sbt._

object PekkoBuildPlugin extends AutoPlugin {
  object autoImport extends PekkoBuildKeys

  private[pekkobuild] lazy val pekkoBuildSettings: Seq[Setting[_]] = Seq(
    LocalRootProject / pekkoMinVersion := "1.0.1"
  )

  override lazy val buildSettings: Seq[Setting[_]] = pekkoBuildSettings

  override def trigger = allRequirements

}
