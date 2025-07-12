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

trait PekkoDependency extends VersionRegex {
  val minVersion: String = "1.0.0"
  val module: Option[String]
  val currentVersion: String

  private lazy val moduleName = module match {
    case Some(mName) => s"pekko.$mName"
    case None        => "pekko" // Main pekko module
  }

  def dependency(defaultVersion: String): Dependency =
    Option(System.getProperty(s"$moduleName.sources")) match {
      case Some(pekkoSources) =>
        Sources(pekkoSources)
      case None =>
        Option(System.getProperty(s"pekko.build.$moduleName.version")) match {
          case Some("main")           => snapshotMain
          case Some("1.0.x")          => snapshot10x
          case Some("1.1.x")          => snapshot11x
          case Some("1.2.x")          => snapshot12x
          case Some("1.3.x")          => snapshot13x
          case Some("1.4.x")          => snapshot14x
          case Some("1.5.x")          => snapshot15x
          case Some("2.0.x")          => snapshot20x
          case Some("latest-release") => latestRelease
          case Some("default") | None => Artifact(defaultVersion)
          case Some(other)            => Artifact(other, isSnapshot = true)
        }
    }

  private lazy val defaultVersion = System.getProperty(s"pekko.build.$moduleName.min.version", currentVersion)
  lazy val default: Dependency    = dependency(defaultVersion)

  lazy val snapshot10x: Artifact   = Artifact(determineLatestSnapshot("1.0"), isSnapshot = true)
  lazy val snapshot11x: Artifact   = Artifact(determineLatestSnapshot("1.1"), isSnapshot = true)
  lazy val snapshot12x: Artifact   = Artifact(determineLatestSnapshot("1.2"), isSnapshot = true)
  lazy val snapshot13x: Artifact   = Artifact(determineLatestSnapshot("1.3"), isSnapshot = true)
  lazy val snapshot14x: Artifact   = Artifact(determineLatestSnapshot("1.4"), isSnapshot = true)
  lazy val snapshot15x: Artifact   = Artifact(determineLatestSnapshot("1.5"), isSnapshot = true)
  lazy val snapshot20x: Artifact   = Artifact(determineLatestSnapshot("2.0"), isSnapshot = true)
  lazy val snapshotMain: Artifact  = Artifact(determineLatestSnapshot(), isSnapshot = true)
  lazy val latestRelease: Artifact = Artifact(determineLatestRelease(), isSnapshot = false)

  lazy val version: String = default match {
    case Artifact(version, _) => version
    case Sources(uri, _)      => uri
  }

  def versionDerivedFromDefault(overrideDefaultPekkoVersion: String): String =
    dependency(overrideDefaultPekkoVersion) match {
      case Artifact(version, _) => version
      case Sources(uri, _)      => uri
    }
}
