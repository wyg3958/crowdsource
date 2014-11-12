#!/bin/sh

docker -H tcp://10.1.42.1:2375 kill crowdsource
# always return 0 to not break the jenkins build
exit 0