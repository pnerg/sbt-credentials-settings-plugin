/**
  *  Copyright 2019 Peter Nerg
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
package org.dmonix.sbt


import org.specs2.matcher.{Matcher, ValueCheck}

import java.io.File
import org.specs2.mutable.Specification
import sbt.librarymanagement.ivy.DirectCredentials

/**
  * Tests for the CredentialSettings class
  * @author Peter Nerg
  */
class CredentialSettingsSpec extends Specification {

  "parsing a dir" >> {
    "shall yield an empty list for an invalid dir" >> {
      val file = new File("src/no-such-dir")
      val credentials = CredentialSettings.publishCredentials(file)
      credentials must beEmpty
    }

    "shall yield an empty list for a dir with no matching files" >> {
      val file = new File("src/test/resources")
      val credentials = CredentialSettings.publishCredentials(file)
      credentials must beEmpty
    }
    
    "shall yield a list with one for directory with only one file" >> {
      val file = new File("src/test/resources/dir1")
      val credentials = CredentialSettings.publishCredentials(file).map(_.asInstanceOf[DirectCredentials])
      credentials must contain(exactly(equal("Sonatype Nexus Repository Manager", "nexus.domain.com", "some-user", "oh-so-secret")))
    }

    "shall yield a list with multiple for directory with multiple files" >> {
      val file = new File("src/test/resources/dir2")
      val credentials = CredentialSettings.publishCredentials(file).map(_.asInstanceOf[DirectCredentials])
      credentials must contain(exactly(
        equal("Sonatype Nexus Repository Manager", "nexus.domain.com", "some-user", "oh-so-secret"),
        equal("Sonatype Nexus Repository Manager", "oss.sonatype.org", "secret-agent", "wont-tell-you")
      ))
    }
  }

  private def equal(realm:String, host:String, user:String, psw:String):Matcher[DirectCredentials] = {
    creds: DirectCredentials =>
      creds.realm === realm
      creds.host === host
      creds.userName === user
      creds.passwd === psw
  }
}
