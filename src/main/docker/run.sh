#!/bin/sh

docker run -p ${it.application.port}:8080 -d --name="crowdsource" "asideas/crowdsource:latest"
# wait a few seconds for app to boot
sleep 10
# curl as health check
curl http://127.0.0.1:${it.application.port}
