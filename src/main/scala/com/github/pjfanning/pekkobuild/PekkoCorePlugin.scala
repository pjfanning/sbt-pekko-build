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

import org.mdedetrich.apache.sonatype.ApacheSonatypePlugin
import org.mdedetrich.apache.sonatype.ApacheSonatypePlugin.autoImport.apacheSonatypeProjectProfile
import sbt._
import sbt.Keys._

object PekkoCorePlugin extends AutoPlugin {

  override lazy val trigger: PluginTrigger = allRequirements
  override lazy val requires: Plugins      = ApacheSonatypePlugin

  object autoImport extends PekkoCoreSettings

  import autoImport._

  override lazy val globalSettings = Seq(
    pekkoCoreProject := false
  )

  override lazy val buildSettings = Seq(
    apacheSonatypeProjectProfile := "pekko",
    // So we don't accidentally release locally,
    // see https://github.com/apache/incubator-pekko-site/wiki/Pekko-Release-Process#deploy-the-jars-to-apache-maven-repository-staging
    LocalRootProject / commands := (LocalRootProject / commands).value.filterNot { command =>
      command.nameOption.exists { name =>
        name.contains("sonatypeRelease") || name.contains("sonatypeBundleRelease")
      }
    }
  )

}
