#!/bin/bash

echo "AWS - MOVING SERVICE FILE..."
sudo mv /home/core/crowdfunding.service /etc/systemd/system

echo "AWS - STOPPING crowdfunding SERVICE..."
fleetctl stop crowdfunding

echo "AWS - DESTROYING crowdfunding IMAGE..."
fleetctl destroy crowdfunding

echo "AWS - LOGGING IN TO DOCKER HUB..."
docker login -e crowdsource@asideas.de -p ideas987 -u asjenkins

echo "AWS - PULLING crowdfunding IMAGE..."
docker pull asideas/crowdsource

echo "AWS - STARTING crowdfunding SERVICE VIA FLEET..."
fleetctl start /etc/systemd/system/crowdfunding.service

STATUS=1

echo "AWS - CHECKING THAT SERVICE IS RUNNING..."
for i in {1..15}
do
 echo "$i one"
 SERVICE_RESULT=$(fleetctl list-units | grep -c crowdfunding.service.*active.*running)
 echo "$i two"
 if [ "$SERVICE_RESULT" == "0" ]; then
  echo "AWS - RUNNING SERVICE NOT DETECTED - WAITING..."
 else
  echo "AWS - RUNNING SERVICE FOUND"
  STATUS=0
  break
 fi
done

echo "AWS - LEAVING AWS..."
exit "$STATUS"