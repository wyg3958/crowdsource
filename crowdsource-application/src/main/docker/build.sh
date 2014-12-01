#!/bin/sh

# clean
docker rmi 'asideas/crowdsource'

# build
cd ${project.build.directory}/docker
cp ${project.build.directory}/${project.build.finalName}.jar .

docker build --rm=true --force-rm=true -t "asideas/crowdsource" .
