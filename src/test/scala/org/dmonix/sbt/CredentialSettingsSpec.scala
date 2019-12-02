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
    
    "shall yield a for directory with only one file" >> {
      val file = new File("src/test/resources/dir1")
      val credentials = CredentialSettings.publishCredentials(file)
      credentials.size === 1
      val creds = credentials.head.asInstanceOf[DirectCredentials]
      creds.realm === "Sonatype Nexus Repository Manager"
      creds.host === "nexus.domain.com"
      creds.userName === "some-user"
      creds.passwd === "oh-so-secret"
    }

    "shall yield a for directory with multiple files" >> {
      val file = new File("src/test/resources/dir2")
      val credentials = CredentialSettings.publishCredentials(file)
      credentials.size === 2
      val creds = credentials.head.asInstanceOf[DirectCredentials]
      creds.realm === "Sonatype Nexus Repository Manager"
      creds.host === "oss.sonatype.org"
      creds.userName === "secret-agent"
      creds.passwd === "wont-tell-you"

      val creds2 = credentials.tail.head.asInstanceOf[DirectCredentials]
      creds2.realm === "Sonatype Nexus Repository Manager"
      creds2.host === "nexus.domain.com"
      creds2.userName === "some-user"
      creds2.passwd === "oh-so-secret"
    }    
  }

}
