/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

name         := "sbt-pekko-build"
organization := "com.github.pjfanning"
description  := "sbt plugin to share certain build classes between Apache Pekko projects"

sbtPlugin := true

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
// compile settings
scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-encoding",
  "UTF-8"
)

scalacOptions ++= {
  if (insideCI.value) {
    val log = sLog.value
    log.info("Running in CI, enabling Scala2 optimizer")
    Seq(
      "-opt-inline-from:<sources>",
      "-opt:l:inline"
    )
  } else Nil
}

ThisBuild / scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)

homepage := Some(url("https://github.com/pjfanning/sbt-pekko-build"))

licenses := Seq("APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

developers := List(
  Developer(id = "pjfanning", name = "PJ Fanning", email = "", url = url("https://github.com/pjfanning"))
)

addSbtPlugin("com.typesafe"   % "sbt-mima-plugin"     % "1.1.3")
addSbtPlugin("org.mdedetrich" % "sbt-apache-sonatype" % "0.1.11")

ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test")))
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(
    RefPredicate.Equals(Ref.Branch("main")),
    RefPredicate.StartsWith(Ref.Tag("v"))
  )
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(name = Some("Build project"), commands = List("test", "scripted"))
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"      -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"          -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD"   -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME"   -> "${{ secrets.SONATYPE_USERNAME }}",
      "CI_SNAPSHOT_RELEASE" -> "+publishSigned"
    )
  )
)

enablePlugins(SbtPlugin)

scriptedLaunchOpts += ("-Dplugin.version=" + version.value)

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false
