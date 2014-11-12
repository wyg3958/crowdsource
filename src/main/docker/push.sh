#!/bin/sh

docker -H tcp://10.1.42.1:2375 login -e crowdsource@asideas.de -p ideas987 -u asjenkins
docker -H tcp://10.1.42.1:2375 push asideas/crowdsource