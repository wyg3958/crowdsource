#!/bin/sh

docker login -e crowdsource@asideas.de -p ideas987 -u asjenkins
docker push asideas/crowdsource:latest
docker logout