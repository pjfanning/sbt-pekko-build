package com.github.pjfanning.pekkobuild

import org.mdedetrich.apache.sonatype.ApacheSonatypePlugin
import org.mdedetrich.apache.sonatype.ApacheSonatypePlugin.autoImport.apacheSonatypeProjectProfile
import sbt._
import sbt.Keys._

object PekkoCorePlugin extends AutoPlugin {

  override lazy val trigger: PluginTrigger = allRequirements
  override lazy val requires: Plugins      = ApacheSonatypePlugin

  object autoImport extends PekkoCoreSettings

  import autoImport._

  override lazy val globalSettings = Seq(
    pekkoCoreProject := false
  )

  override lazy val buildSettings = Seq(
    apacheSonatypeProjectProfile := "pekko",
    // So we don't accidentally release locally,
    // see https://github.com/apache/incubator-pekko-site/wiki/Pekko-Release-Process#deploy-the-jars-to-apache-maven-repository-staging
    LocalRootProject / commands := (LocalRootProject / commands).value.filterNot { command =>
      command.nameOption.exists { name =>
        name.contains("sonatypeRelease") || name.contains("sonatypeBundleRelease")
      }
    }
  )

}
