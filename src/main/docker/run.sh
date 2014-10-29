#!/bin/sh

docker run -p ${it.application.port}:8080 -d --name="crowdsource" "asideas/crowdsource:latest"
curl http://127.0.0.1:${it.application.port}/hello
curl http://127.0.0.1:${it.application.port}
