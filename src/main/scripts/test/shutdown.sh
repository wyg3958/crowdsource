#!/bin/sh

# kill application
docker kill crowdsource

# kill database
docker kill crowdsourcedb
docker rm crowdsourcedb

# always return 0 to not break the jenkins build
exit 0