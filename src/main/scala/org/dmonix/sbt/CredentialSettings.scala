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

import sbt._

import scala.io.Source

/**
  * Settings to be used when configuring the build.sbt file with credentials for publishing to a binary repository.
  *
  * The class provides support for `~/.ivy2/.credentials` files containing multiple realm entries.
  *
  * Normally one would only have a single realm but it creates problems if one works with multiple repositories.
  *
  * Example. of `~/.ivy2/.credentials` contents
  * {{{
  * #Local Nexus installation
  * realm=Sonatype Nexus Repository Manager
  * host=somehost.your.domain
  * user=peter
  * password=oh-so-secret
  *
  * #Local Artifactory installation
  * realm=Artifactory Realm
  * host=somehost.your.domain
  * user=peter
  * password=yet-another-psw
  *
  * #Maven Central
  * realm=Sonatype Nexus Repository Manager
  * host=oss.sonatype.org
  * user=peter
  * password=wont-tell-you
  * }}}
  * @author Peter Nerg
  */
object CredentialSettings {
  /**
    * Returns the credentials to use when deploying the artifact to the repository.
    *
    * The functions reads all settings from the `~/.ivy2/.credentials` file.
    *
    * Example on usage in build.sbt:
    * {{{
    * import org.dmonix.sbt.CredentialsSettings._
    * credentials ++= publishCredentials
    * }}}
    * @return A sequence with the credentials to use
    */
  def publishCredentials: Seq[Credentials] = {
    val ivyCredentials = Path.userHome / ".ivy2" / ".credentials"
    //if we find a .ivy/.credentials file we read all settings from the file
    (ivyCredentials.asFile) match {
      case (creds) if creds.canRead =>
        publishCredentials(creds)
      case _ => Nil
    }
  }
  
  /**
    * Returns the credentials to use when deploying the artifact to the repository.
    *
    * The functions reads all settings from the provided file.
    *
    * Example on usage in build.sbt:
    * {{{
    * import org.dmonix.sbt.CredentialsSettings._
    * credentials ++= publishCredentials(file("filePath"))
    * }}}
    *
    * @param credentialsFile The file containing the credentials
    * @return A sequence with the credentials to use
    */
  def publishCredentials(credentialsFile:File): Seq[Credentials] = {
    var realms = Seq[String]()
    var hosts = Seq[String]()
    var users = Seq[String]()
    var passwords = Seq[String]()

    //if we find a .ivy/.credentials file we read all settings from the file
    if (credentialsFile.canRead) {

      for (line <- Source.fromFile(credentialsFile).getLines()) {
        if(line.startsWith("realm")) {
          realms ++= Seq(getValue(line))
        }
        else if(line.startsWith("host")) {
          hosts ++= Seq(getValue(line))
        }
        else if(line.startsWith("user")) {
          users ++= Seq(getValue(line))
        }
        else if(line.startsWith("password")) {
          passwords ++= Seq(getValue(line))
        }
      }
      //zip all lists into a single list with a Tuple4
      val creds = realms zip hosts zip users zip passwords map {
        case (((a,b),c),d) => (a,b,c,d)
      }
      creds.map(c => Credentials(c._1, c._2, c._3, c._4))
    }
    else
      Nil
  }

  private def getValue(line:String):String = line.substring(line.indexOf("=")+1)
}
