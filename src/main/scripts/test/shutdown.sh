#!/bin/sh

# kill application
docker kill crowdsource_test

# kill database
docker kill crowdsourcetestdb
docker rm crowdsourcetestdb

# always return 0 to not break the jenkins build
exit 0