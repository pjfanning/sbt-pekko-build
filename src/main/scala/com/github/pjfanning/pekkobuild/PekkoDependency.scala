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

import sbt._
import sbt.Keys._

import scala.util.matching.Regex.Groups

object PekkoDependency {

  sealed trait Pekko {
    def version: String
    // The version to use in api/japi/docs links,
    // so 'x.y', 'x.y.z', 'current' or 'snapshot'
    def link: String
  }
  case class Artifact(version: String, isSnapshot: Boolean) extends Pekko {
    override def link = VersionNumber(version) match { case VersionNumber(Seq(x, y, _*), _, _) => s"$x.$y" }
  }
  object Artifact {
    def apply(version: String): Artifact = {
      val isSnap = version.endsWith("-SNAPSHOT")
      new Artifact(version, isSnap)
    }
  }
  case class Sources(uri: String, link: String = "current") extends Pekko {
    def version = link
  }

  def pekkoDependency(defaultVersion: String): Pekko =
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
  val default                     = pekkoDependency(defaultPekkoVersion)

  lazy val snapshot10x   = Artifact(determineLatestSnapshot("1.0"), true)
  lazy val snapshotMain  = Artifact(determineLatestSnapshot(), true)
  lazy val latestRelease = Artifact(determineLatestRelease(), false)

  val pekkoVersion: String = default match {
    case Artifact(version, _) => version
    case Sources(uri, _)      => uri
  }

  def pekkoVersionDerivedFromDefault(overrideDefaultPekkoVersion: String): String =
    pekkoDependency(overrideDefaultPekkoVersion) match {
      case Artifact(version, _) => version
      case Sources(uri, _)      => uri
    }

  implicit class RichProject(project: Project) {

    /** Adds either a source or a binary dependency, depending on whether the above settings are set */
    def addPekkoModuleDependency(module: String, config: String = "", pekko: Pekko = default): Project =
      pekko match {
        case Sources(sources, _) =>
          val moduleRef = ProjectRef(uri(sources), module)
          val withConfig: ClasspathDependency =
            if (config == "") moduleRef
            else moduleRef % config

          project.dependsOn(withConfig)
        case Artifact(pekkoVersion, pekkoSnapshot) =>
          project.settings(
            libraryDependencies += {
              if (config == "")
                "org.apache.pekko" %% module % pekkoVersion
              else
                "org.apache.pekko" %% module % pekkoVersion % config
            },
            resolvers ++= (if (pekkoSnapshot)
                             Seq(Resolver.ApacheMavenSnapshotsRepo)
                           else Nil)
          )
      }
  }

  private def determineLatestSnapshot(prefix: String = ""): String = determineLatestVersion(true, prefix)

  private def determineLatestRelease(prefix: String = ""): String = determineLatestVersion(false, prefix)

  private def determineLatestVersion(useSnapshots: Boolean, prefix: String): String = {
    import sbt.librarymanagement.Http.http
    import gigahorse.GigahorseSupport.url
    import scala.concurrent.Await
    import scala.concurrent.duration._

    val regex =
      if (useSnapshots) """href=".*/((\d+)\.(\d+)\.(\d+)(-(M|RC)(\d+))?\+(\d+)-[0-9a-f]+-SNAPSHOT)/""""
      else """>.*((\d+)\.(\d+)\.(\d+)(-(M|RC)(\d+))?)/<"""
    val versionR = regex.r
    val repo     = if (useSnapshots) Resolver.ApacheMavenSnapshotsRepo.root else Resolver.DefaultMavenRepositoryRoot

    // pekko-cluster-sharding-typed_2.13 seems to be the last nightly published by `pekko-publish-nightly` so if that's there then it's likely the rest also made it
    val body = Await
      .result(
        http.run(url(s"${repo}org/apache/pekko/pekko-cluster-sharding-typed_2.13/")),
        10.seconds
      )
      .bodyAsString

    // we use tagNumber set as Integer.MAX_VALUE when there is no tagNumber
    // this ensures that RC and Milestone versions are treated as older than non-RC/non-milestone versions
    val allVersions =
      if (useSnapshots)
        versionR
          .findAllMatchIn(body)
          .map { case Groups(full, ep, maj, min, _, _, tagNumber, offset) =>
            (ep.toInt,
             maj.toInt,
             min.toInt,
             Option(tagNumber).map(_.toInt).getOrElse(Integer.MAX_VALUE),
             offset.toInt
            ) -> full
          }
          .filter(_._2.startsWith(prefix))
          .toVector
          .sortBy(_._1)
      else
        versionR
          .findAllMatchIn(body)
          .map { case Groups(full, ep, maj, min, _, _, tagNumber) =>
            (ep.toInt, maj.toInt, min.toInt, Option(tagNumber).map(_.toInt).getOrElse(Integer.MAX_VALUE)) -> full
          }
          .filter(_._2.startsWith(prefix))
          .toVector
          .sortBy(_._1)

    allVersions.last._2
  }
}
