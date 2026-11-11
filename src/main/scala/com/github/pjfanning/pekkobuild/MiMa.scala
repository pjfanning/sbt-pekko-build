/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * license agreements; and to You under the Apache License, version 2.0:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is part of the Apache Pekko project, which was derived from Akka.
 */

/*
 * Copyright (C) 2009-2020 Lightbend Inc. <https://www.lightbend.com>
 */

package com.github.pjfanning.pekkobuild

import sbt._
import sbt.Keys._
import com.typesafe.tools.mima.plugin.MimaPlugin
import com.typesafe.tools.mima.plugin.MimaPlugin.autoImport._

object MiMa extends AutoPlugin {

  private val defaultPreviousVersion = "1.0.0"

  override def requires = MimaPlugin

  override def trigger = allRequirements

  object autoImport {
    val pekkoMimaPreviousVersion =
      settingKey[String]("The previously released version that MiMa checks binary compatibility against")
  }

  import autoImport._

  val checkMimaFilterDirectories =
    taskKey[Unit]("No-op, kept for compatibility")

  override lazy val globalSettings = Seq(
    pekkoMimaPreviousVersion := defaultPreviousVersion
  )

  override val projectSettings = Seq(
    mimaReportSignatureProblems := true,
    mimaPreviousArtifacts := pekkoPreviousArtifacts(name.value, organization.value, pekkoMimaPreviousVersion.value),
    checkMimaFilterDirectories := {}
  )

  @deprecated("filter directories are named per minor version (e.g. 1.0.x.backwards.excludes); this is a no-op",
              "0.5.0"
  )
  def checkFilterDirectories(moduleRoot: File): Unit = ()

  def pekkoPreviousArtifacts(projectName: String, organization: String): Set[sbt.ModuleID] =
    pekkoPreviousArtifacts(projectName, organization, defaultPreviousVersion)

  def pekkoPreviousArtifacts(projectName: String, organization: String, previousVersion: String): Set[sbt.ModuleID] =
    Set(organization %% projectName % previousVersion)
}
