#!/bin/sh

docker kill crowdsource
# always return 0 to not break the jenkins build
exit 0