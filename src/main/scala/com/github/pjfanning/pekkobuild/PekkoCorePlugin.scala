package com.github.pjfanning.pekkobuild

import org.mdedetrich.apache.sonatype.ApacheSonatypePlugin
import org.mdedetrich.apache.sonatype.ApacheSonatypePlugin.autoImport.apacheSonatypeProjectProfile
import sbt._

object PekkoCorePlugin extends AutoPlugin {

  override lazy val trigger: PluginTrigger = allRequirements
  override lazy val requires: Plugins      = ApacheSonatypePlugin

  object autoImport extends PekkoCoreSettings

  import autoImport._

  override lazy val globalSettings = Seq(
    pekkoCoreProject := false
  )

  override lazy val buildSettings = Seq(
    apacheSonatypeProjectProfile := "pekko"
  )

}
