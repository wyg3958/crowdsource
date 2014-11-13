#!/bin/sh

# clean
docker kill crowdsource
docker rm crowdsource
docker rmi 'asideas/crowdsource'

# build
cd ${project.build.directory}/docker
cp ${project.build.directory}/${project.build.finalName}.jar .

docker build -t "asideas/crowdsource" .
