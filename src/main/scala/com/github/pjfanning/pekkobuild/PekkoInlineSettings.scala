package com.github.pjfanning.pekkobuild

import sbt.{SettingKey, settingKey}

trait PekkoInlineSettings {
  lazy val pekkoInlineEnabled: SettingKey[Boolean] = settingKey(
    "Whether to enable the Scala 2 inliner for Pekko modules." +
      "Defaults to pekko.no.inline property"
  )
}
