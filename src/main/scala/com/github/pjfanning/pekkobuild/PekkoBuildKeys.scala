package com.github.pjfanning.pekkobuild

import sbt.{SettingKey, settingKey}

trait PekkoBuildKeys {
  lazy val pekkoMinVersion: SettingKey[String] =
    settingKey[String]("Minimum version of Apache Pekko Core to use")
}
