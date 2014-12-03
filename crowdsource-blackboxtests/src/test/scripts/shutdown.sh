#!/bin/sh

# kill application
docker kill crowdsource_test
docker rm crowdsource_test

# kill database
docker kill crowdsourcetestdb
docker rm crowdsourcetestdb

# kill mailserver
docker kill crowdsourcetestmailserver
docker rm crowdsourcetestmailserver

# always return 0 to not break the jenkins build
exit 0