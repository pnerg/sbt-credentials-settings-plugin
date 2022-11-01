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

  val ENV_REALM = "CREDENTIAL_REALM"
  val ENV_HOST = "CREDENTIAL_HOST"
  val ENV_USER =  "CREDENTIAL_USER"
  val ENV_PASSWORD = "CREDENTIAL_PASSWORD"

  //matches any '${NAME}' pattern
  private val regex = raw"(\$$\{\w+\})".r

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
    * @since 1.0
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
    * Generates a sequence with a single Credential value if certain ENV/sys.props are set.
    * @return
    * @since 1.1
    */
  def publishCredentialsFromEnv():Seq[Credentials] = publishCredentialsFromEnv(ENV_REALM, ENV_HOST, ENV_USER, ENV_PASSWORD)

  /**
    * Generates a sequence with a single Credential value if provided ENV/sys.props are set.
    *
    * @return
    * @since 1.1
    */
  def publishCredentialsFromEnv(realmName:String, hostName:String, userName:String, passwordName:String): Seq[Credentials] = {
    Seq(for {
      realm <- envOrProp(realmName)
      host <- envOrProp(hostName)
      user <- envOrProp(userName)
      psw <- envOrProp(passwordName)
    } yield {
      Credentials(realm, host, user, psw)
    }).flatten
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
      realm <- Option(properties.getProperty("realm")).map(replaceEnvVar)
      host <- Option(properties.getProperty("host")).map(replaceEnvVar)
      user <- Option(properties.getProperty("user")).map(replaceEnvVar)
      psw <- Option(properties.getProperty("password")).map(replaceEnvVar)
    } yield {
      Credentials(realm, host, user, psw)
    }
  }

  private[sbt] def replaceEnvVar(string:String):String = {
    regex.findAllMatchIn(string)
      .map(m => string.substring(m.start+2,m.end-1)) //finds all ${NAME} in the string and extracts only 'NAME'
      .map(envOrPropElseFail) //finds the ENV/sys.prop with the corresponding name
      .foldLeft(string){(acc,v) =>
        //finds and replaces the ${NAME} with the value of the env/sys.prop
        acc.replaceAll(raw"\$$\{"+v._1+"}", v._2)
      }
  }

  private def envOrPropElseFail(name:String):(String,String) = {
    (
      name,
      envOrProp(name).getOrElse(sys.error(s"Could not resolve ENV or sys prop with the name $name"))
    )
  }

  private def envOrProp(name: String): Option[String] = sys.env.get(name).orElse(sys.props.get(name))

}
