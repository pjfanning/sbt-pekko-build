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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PekkoCoreDependencySpec extends AnyWordSpec with Matchers {
  "PekkoCoreDependency" should {
    "eval pekkoVersionDerivedFromDefault" in {
      PekkoCoreDependency.versionDerivedFromDefault("1.0.2") shouldEqual "1.0.2"
    }
    "eval doc link" in {
      PekkoCoreDependency.default.link shouldEqual "1.0"
    }
    // following tests are useful for manual testing but requires internet calls that may fail
    "eval snapshot dependency with system property" ignore {
      System.setProperty("pekko.build.pekko.version", "2.x")
      try
        // this value will change as new snapshots are released
        PekkoCoreDependency.dependency("1.0.0").version shouldEqual "2.0.0-M0+319-00b44f37-SNAPSHOT"
      finally
        System.clearProperty("pekko.build.pekko.version")
    }
    "eval latest dependency with system property" ignore {
      System.setProperty("pekko.build.pekko.version", "latest-release")
      try
        // this value will change as new releasee appear
        PekkoCoreDependency.dependency("1.0.0").version shouldEqual "2.0.0-M1"
      finally
        System.clearProperty("pekko.build.pekko.version")
    }
  }
}
