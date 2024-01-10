package com.github.pjfanning.pekkobuild

import sbt._

object PekkoCorePlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport extends PekkoCoreSettings

  import autoImport._

  override lazy val globalSettings = Seq(
    pekkoCoreProject := false
  )

}
