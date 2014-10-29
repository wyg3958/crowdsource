#!/bin/sh

cd ${project.build.directory}/docker
mv ${project.build.directory}/${project.build.finalName}.jar .

docker build -t "asideas/crowdsource:latest" .
