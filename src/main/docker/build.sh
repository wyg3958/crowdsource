#!/bin/sh

cd ${project.build.directory}/docker
cp ${project.build.directory}/${project.build.finalName}.jar .

docker build -t "asideas/crowdsource" .
