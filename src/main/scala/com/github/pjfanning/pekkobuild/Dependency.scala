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
 
 package com.github.pjfanning.pekkobuild

import sbt.VersionNumber

sealed trait Dependency {
  def version: String
  // The version to use in api/japi/docs links,
  // so 'x.y', 'x.y.z', 'current' or 'snapshot'
  def link: String
}

case class Artifact(version: String, isSnapshot: Boolean) extends Dependency {
  override def link: String =
    VersionNumber(version) match { case VersionNumber(Seq(x, y, _*), _, _) => s"$x.$y" }
}

object Artifact {
  def apply(version: String): Artifact = {
    val isSnap = version.endsWith("-SNAPSHOT")
    new Artifact(version, isSnap)
  }
}
case class Sources(uri: String, link: String = "current") extends Dependency {
  def version: String = link
}
