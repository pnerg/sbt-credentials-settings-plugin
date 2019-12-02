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

import java.io.FileInputStream

import sbt._

import java.util.Properties

/**
  * Settings to be used when configuring the build.sbt file with credentials for publishing to a binary repository.
  *
  * The class provides support for `~/.ivy2/.credentials` files containing multiple realm entries.
  *
  * Normally one would only have a single realm but it creates problems if one works with multiple repositories.
  *
  * Example. of `~/.ivy2/mvn-central.credentials` contents
  * {{{
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
    * The functions reads settings from all `~/.ivy2/%.credentials` files.
    *
    * Example on usage in build.sbt:
    * {{{
    * import org.dmonix.sbt.CredentialsSettings._
    * credentials ++= publishCredentials
    * }}}
    * @return A sequence with the credentials to use
    */
  def publishCredentials(dir:File): Seq[Credentials] = {
    if(dir.isDirectory) {
      dir.listFiles()
        .filter(f => f.isFile && f.canRead)
        .filter(_.getName.endsWith(".credentials"))
        .map(parseFile)
        .toSeq
        .flatten
    }
    else {
      Nil
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
  private def parseFile(credentialsFile:File): Option[Credentials] = {
    val properties = new Properties()
    properties.load(new FileInputStream(credentialsFile))
    
    for {
      realm <- Option(properties.getProperty("realm"))
      host <- Option(properties.getProperty("host"))
      user <- Option(properties.getProperty("user"))
      psw <- Option(properties.getProperty("password"))
    } yield {
      Credentials(realm, host, user, psw)
    }
  }
}
