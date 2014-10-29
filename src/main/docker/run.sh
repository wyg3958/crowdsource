#!/bin/sh

docker run -p ${it.application.port}:8080 -d --name="crowdsource" "asideas/crowdsource:latest"