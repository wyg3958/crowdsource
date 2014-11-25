#!/bin/bash

echo "AWS - MOVING SERVICE FILE..."
sudo mv /home/core/crowdsource.service /etc/systemd/system

echo "AWS - STOPPING crowdsource SERVICE..."
fleetctl stop crowdsource

echo "AWS - DESTROYING crowdsource IMAGE..."
fleetctl destroy crowdsource

echo "AWS - LOGGING IN TO DOCKER HUB..."
docker login -e crowdsource@asideas.de -p ideas987 -u asjenkins

echo "AWS - PULLING crowdsource IMAGE..."
docker pull asideas/crowdsource

echo "AWS - STARTING crowdsource SERVICE VIA FLEET..."
fleetctl start /etc/systemd/system/crowdsource.service

STATUS=1

echo "AWS - CHECKING THAT SERVICE IS RUNNING..."
for i in {1..20}
do
 SERVICE_RESULT=$(fleetctl list-units | grep crowdsource.service.*active.*running)
 if [ "$SERVICE_RESULT" == "" ]; then
  echo "AWS - RUNNING SERVICE NOT DETECTED - WAITING..."
  sleep 2s
 else
  echo "AWS - RUNNING SERVICE FOUND"
  STATUS=0
  break
 fi
done

echo "AWS - LEAVING AWS..."
exit "$STATUS"