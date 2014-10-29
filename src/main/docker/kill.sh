#!/bin/sh

docker kill crowdsource
docker rm crowdsource
docker rmi 'asideas/crowdsource'

# if any of the above fails (probably because there is no image or there is no container)
# still return 0 to not break the jenkins build
exit 0