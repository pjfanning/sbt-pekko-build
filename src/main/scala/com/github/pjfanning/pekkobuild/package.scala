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

package com.github.pjfanning

import sbt._
import sbt.Keys.{libraryDependencies, resolvers}
import sbt.{ClasspathDependency, Project, ProjectRef, Resolver, uri}

package object pekkobuild {
  implicit class RichProject(project: Project) {

    /** Adds either a source or a binary dependency, depending on whether the above settings are set */
    def addPekkoModuleDependency(module: String, config: String = "", pekko: Dependency): Project =
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
}
