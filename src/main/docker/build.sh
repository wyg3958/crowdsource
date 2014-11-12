#!/bin/sh

# clean
docker -H tcp://10.1.42.1:2375 kill crowdsource
docker -H tcp://10.1.42.1:2375 rm crowdsource
docker -H tcp://10.1.42.1:2375 rmi 'asideas/crowdsource'

# build
cd ${project.build.directory}/docker
cp ${project.build.directory}/${project.build.finalName}.jar .

docker -H tcp://10.1.42.1:2375 build -t "asideas/crowdsource" .
