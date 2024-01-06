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

object PekkoDependency extends VersionRegex {
  override val checkProject: String = "pekko-cluster-sharding-typed"

  def pekkoDependency(defaultVersion: String): Dependency =
    Option(System.getProperty("pekko.sources")) match {
      case Some(pekkoSources) =>
        Sources(pekkoSources)
      case None =>
        Option(System.getProperty("pekko.build.pekko.version")) match {
          case Some("main")           => snapshotMain
          case Some("1.0.x")          => snapshot10x
          case Some("latest-release") => latestRelease
          case Some("default") | None => Artifact(defaultVersion)
          case Some(other)            => Artifact(other, true)
        }
    }

  private val defaultPekkoVersion = System.getProperty("pekko.build.pekko.min.version", "1.0.2")
  val minPekkoVersion: String     = "1.0.0"
  lazy val default: Dependency    = pekkoDependency(defaultPekkoVersion)

  lazy val snapshot10x   = Artifact(determineLatestSnapshot("1.0"), true)
  lazy val snapshotMain  = Artifact(determineLatestSnapshot(), true)
  lazy val latestRelease = Artifact(determineLatestRelease(), false)

  lazy val pekkoVersion: String = default match {
    case Artifact(version, _) => version
    case Sources(uri, _)      => uri
  }

  def pekkoVersionDerivedFromDefault(overrideDefaultPekkoVersion: String): String =
    pekkoDependency(overrideDefaultPekkoVersion) match {
      case Artifact(version, _) => version
      case Sources(uri, _)      => uri
    }
}
