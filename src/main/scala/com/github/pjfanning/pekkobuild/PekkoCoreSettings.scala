package com.github.pjfanning.pekkobuild

import sbt._

trait PekkoCoreSettings {
  lazy val pekkoCoreProject: SettingKey[Boolean] = settingKey(
    "Whether this is the core Pekko project or a Pekko" +
      " module. Defaults to false"
  )
}
