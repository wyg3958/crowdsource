#!/bin/sh

# clean
docker kill crowdsource
docker rm crowdsource
docker rmi 'asideas/crowdsource'

# build
cd ${project.build.directory}/scripts/build
cp ${project.build.directory}/${project.build.finalName}.jar .

docker build --rm=true --force-rm=true -t "asideas/crowdsource" .
