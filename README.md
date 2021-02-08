![Build & Test](https://github.com/pnerg/sbt-credentials-settings-plugin/workflows/Build%20&%20Test/badge.svg) 
[![codecov](https://codecov.io/gh/pnerg/sbt-credentials-settings-plugin/branch/master/graph/badge.svg?token=qHGsIGxheH)](https://codecov.io/gh/pnerg/sbt-credentials-settings-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.dmonix.sbt/sbt-credentials-settings-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.dmonix.sbt/sbt-credentials-settings-plugin)

# SBT  Credentials Settings Plugin
SBT plugin for settings the credentials needed to push to a binary repository

In order to be able to publish to any repository it generally requires you to provide credentials to that repository.  
Traditionally sbt uses the _~/.ivy2/.credentials_ file for that.  
It however requires some manual setup in the build.sbt file.  
This plugin will automatically try to parse all files ending with _.credentials_ in the path _~/.ivy2/_  

The examples below are a fully working _.credentials_ files.  
  
e.g _.credentials_   
```script
#Local Nexus installation
realm=Sonatype Nexus Repository Manager
host=nexus.domain.com
user=some-user
password=oh-so-secret
```

e.g. _artifactory.credentials_
```script
#Local Artifactory installation
realm=Artifactory Realm
host=artifactory.domain.net
user=yet-another-user
password=yet-another-psw
```

e.g. _mvn-central.credentials_
```script
#Maven Central
realm=Sonatype Nexus Repository Manager
host=oss.sonatype.org
user=secret-agent
password=wont-tell-you
```

The plugin provided by this project is an auto-plugin so once added to the project it will automatically read all _.credential_ files in  _~/.ivy2/_.

Simply add this to the _plugins.sbt_ file:
```script
addSbtPlugin("org.dmonix.sbt" % "sbt-credentials-settings-plugin" % "1.0.0")
```

To test that the plugin reads you local credentials file try the command
```script
sbt 'show credentials'
```
It will print something like this, representing the list of all three credentials it found in the examples above.
```script
[info]  List(sbt.librarymanagement.ivy.DirectCredentials@66859669, sbt.librarymanagement.ivy.DirectCredentials@6e591c03, sbt.librarymanagement.ivy.DirectCredentials@7115d5af)
[
```