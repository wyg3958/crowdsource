crowdsource
===========

DEVELOPERS! 

DOCKER
Enable docker-profile (-Pdocker) to boot the application for integration tests (works only on linux hosts with docker installed)
=> If you want to run the integration tests on windows or mac you need to run CrowdSourceConfig.main() to have the app running

PHANTOMJS/CHROME/FIREFOX
Enable phantomjs-profile (-Pphantomjs) to use phantomjs (will be downloaded). 
Specifiy path to chromedriver/chrom in test.properties to use chrome (untested). 
Firefox is the fallback. Do nothing and use the worst possible browser by default.
