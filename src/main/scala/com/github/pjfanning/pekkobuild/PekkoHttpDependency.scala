/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * license agreements; and to You under the Apache License, version 2.0:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is part of the Apache Pekko project, which was derived from Akka.
 */

/*
 * Copyright (C) 2017-2020 Lightbend Inc. <https://www.lightbend.com>
 */

package com.github.pjfanning.pekkobuild

object PekkoHttpDependency extends VersionRegex {
  override val checkProject: String = "pekko-http-testkit"

  def pekkoHttpDependency(defaultVersion: String): Dependency =
    Option(System.getProperty("pekko.http.sources")) match {
      case Some(pekkoSources) =>
        Sources(pekkoSources)
      case None =>
        Option(System.getProperty("pekko.build.pekko.http.version")) match {
          case Some("main")           => snapshotMain
          case Some("1.0.x")          => snapshot10x
          case Some("latest-release") => latestRelease
          case Some("default") | None => Artifact(defaultVersion)
          case Some(other)            => Artifact(other, true)
        }
    }

  private val defaultPekkoHttpVersion = System.getProperty("pekko.build.pekko.http.min.version", "1.0.0")
  val default: Dependency             = pekkoHttpDependency(defaultPekkoHttpVersion)

  lazy val snapshot10x   = Artifact(determineLatestSnapshot("1.0"), true)
  lazy val snapshotMain  = Artifact(determineLatestSnapshot(), true)
  lazy val latestRelease = Artifact(determineLatestRelease(), false)

  val pekkoHttpVersion: String = default match {
    case Artifact(version, _) => version
    case Sources(uri, _)      => uri
  }

  def pekkoHttpVersionDerivedFromDefault(overrideDefaultPekkoHttpVersion: String): String =
    pekkoHttpDependency(overrideDefaultPekkoHttpVersion) match {
      case Artifact(version, _) => version
      case Sources(uri, _)      => uri
    }
}
