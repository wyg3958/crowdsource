crowdsource
===========

DEVELOPERS! 

MONGO-DB

Provide properties: de.axelspringer.ideas.crowdsource.db. host/port/name.

DOCKER

Enable docker-profile (-Pdocker) to boot the application for integration tests (works only on linux hosts with docker installed)
=> If you want to run the integration tests on windows or mac you need to run CrowdSourceConfig.main() to have the app running

JS-Tests
1: install node.js/npm
# TODO: wtf? i am not yet getting the difference here. but when only installing with '-g' some plugins will not be available for karma-cli
2: run 'npm install -g karma karma-cli karma-phantomjs-launcher karma-jasmine karma-jasmine-jquery karma-junit-reporter karma-ng-html2js-preprocessor'
3: run 'npm install karma karma-cli karma-phantomjs-launcher karma-jasmine karma-jasmine-jquery karma-junit-reporter karma-ng-html2js-preprocessor'


PHANTOMJS/CHROME/FIREFOX

Enable phantomjs-profile (-Pphantomjs) to use phantomjs (will be downloaded). 
Specifiy path to chromedriver/chrom in test.properties to use chrome (untested). 
Firefox is the fallback. Do nothing and use the worst possible browser by default.


TEAMCITY

To make the tests run in your TeamCity build add a configuration parameter to your build (e.g. "DOCKERHOST").
Set it's value to the name of the server where Crowdsource is reachable.
Give this parameter in your build's maven step: clean deploy -Pphantomjs,docker -Dapplicationhost=%DOCKERHOST%

