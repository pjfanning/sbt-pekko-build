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

import sbt.Resolver

import scala.util.matching.Regex.Groups

trait VersionRegex {
  // choose a project that is one of the last to be published (eg `pekko-cluster-sharding-typed`)
  // `_2.13` is assumed
  protected val checkProject: String

  protected def determineLatestSnapshot(prefix: String = ""): String =
    determineLatestVersion(useSnapshots = true, prefix)

  protected def determineLatestRelease(prefix: String = ""): String =
    determineLatestVersion(useSnapshots = false, prefix)

  protected def determineLatestVersion(useSnapshots: Boolean, prefix: String): String = {
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
        http.run(url(s"${repo}org/apache/pekko/${checkProject}_2.13/")),
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
