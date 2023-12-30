package com.github.pjfanning.pekkobuild

import sbt.{SettingKey, settingKey}

trait PekkoInlineSettings {
  lazy val pekkoInlineEnabled: SettingKey[Boolean] = settingKey("Whether to enable the Scala 2 inliner for Pekko modules." +
    "Defaults to pekko.no.inline property")
  lazy val pekkoInlineCoreProject: SettingKey[Boolean] = settingKey("Whether this is the core Pekko project or a Pekko module " +
    "since the core Pekko project has different inline settings. Defaults to false")
}
