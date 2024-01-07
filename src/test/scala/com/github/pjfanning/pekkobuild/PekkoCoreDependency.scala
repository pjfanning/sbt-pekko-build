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

object PekkoCoreDependency extends PekkoDependency {
  override val checkProject: String = "pekko-cluster-sharding-typed"
  override val module: Option[String] = None
  override val currentVersion: String = "1.0.2"
}
