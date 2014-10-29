#!/bin/sh

# -e igaffai@gmail.com
docker login -p ideas987 -u asjenkins
docker push asideas/crowdsource:latest
docker logout