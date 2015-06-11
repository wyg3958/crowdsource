# crowdsource
a collaborative crowd sourcing and funding tool using virtual currency

Current build status: [![Build Status](https://travis-ci.org/as-ideas/crowdsource.svg?branch=master)](https://travis-ci.org/as-ideas/crowdsource)

documentation
=============

Modules
-------
crowdsource-frontend
The frontend-module contains the angularjs client app along with js-tests for it. It will be bundled in a jar file and then integrated into the core-app.

crowdsource-core
The core-module contains the backend for crowdsource. It integrates the resources-jar generated by frontend-module.

crowdsource-example
Example crowdsource-application. You can use this as a basis for your own build.

crowdsource-integrationtests
Some end-to-end-, integration-, acceptance-, blackbox-, you-name-it-tests.


Example developer run config
----------------------------
java -jar crowdsource-example.jar


Token signing
-------------
CrowdSource uses JWT with a token signing key. These (tokensigningkey/tokensigningkey.pub) must be present under /src/main/resources.
You can generate a pair of your own using ssh-keygen.


Persistence
-----------
CrowdSource uses MongoDB. You should provide the DB hosts comma-separated via property de.axelspringer.ideas.crowdsource.db.hosts.
Eg java -jar ... -Dde.axelspringer.ideas.crowdsource.db.hosts=hosta,hostb,hostc
For local development you can use the embed-mongo-maven-plugin that is used in the integrationstests as well.
Simply run mvn com.github.joelittlejohn.embedmongo:embedmongo-maven-plugin:0.1.12:start -Dembedmongo.wait in the crowdsource-integrationtests module.

For configuration your own application:
- Create a appication.properties with following entries
> de.axelspringer.ideas.crowdsource.db.hosts=127.0.0.1
> de.axelspringer.ideas.crowdsource.db.port=27017
> de.axelspringer.ideas.crowdsource.db.name=crowdsource
> de.axelspringer.ideas.crowdsource.db.username=crowdsource
> de.axelspringer.ideas.crowdsource.db.password=

You can use a comma seperated list for multiple db-hosts, eg.
> de.axelspringer.ideas.crowdsource.db.hosts=127.0.0.1,127.0.0.2


Mailserver
----------
CrowdSource sends Mails for some occassions. In the example-app a mailserver is started and the mails are exposed via a rest endpoint under /mails.
This is handy for local development as well as integration testing.

For the configuration you need the following entries in your application.properties:
> de.axelspringer.ideas.crowdsource.mail.host=smtp.xyzn.org
> de.axelspringer.ideas.crowdsource.mail.port=587
> de.axelspringer.ideas.crowdsource.mail.username=admin@crowd.yourname.de
> de.axelspringer.ideas.crowdsource.mail.password=
> de.axelspringer.ideas.crowdsource.mail.starttls=true

The default-configuration uses localhost:1025 with no username and no password. StartTls is false per default.


Frontend
--------

During maven build, frontend-maven-plugin will take care of downloading all tools and dependencies required for the frontend build.
To execute manually you will need node/npm. See scripts in package.json for npm.
You will also need to run 'npm install -g karma-cli'.


AT-Browsers: PHANTOMJS/CHROME/FIREFOX
-------------------------------------

There were some issues with phantomjs. Feel free to give it another try. Maybe its fixed?
Specifiy path to chromedriver/chrome in test.properties to use chrome (environment variable also possible).
Firefox is the fallback. Do nothing and use the worst browser in the world by default.

Maven
-----
```xml
<dependencies>
    <dependency>
        <groupId>de.axelspringer.ideas.crowdsource</groupId>
        <artifactId>crowdsource-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>crowdsource</id>
        <url>https://raw.github.com/as-ideas/crowdsource/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```