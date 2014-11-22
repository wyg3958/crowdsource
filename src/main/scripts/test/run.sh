#!/bin/bash

# kill mongodb (if exists)
docker kill crowdsourcedb
docker rm crowdsourcedb
# run mongodb
docker run -d -p 27017:27017 -p 28017:28017 --name crowdsourcedb dockerfile/mongodb

# let db get awake
sleep 2
echo "outputting logs of mongodb-container"
docker logs crowdsourcedb

# get host ip
HOST_IP=`netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}'`

# run application
docker run -p ${it.application.port}:8080 -d -e de.axelspringer.ideas.crowdsource.db.host=$HOST_IP --name="crowdsource" "asideas/crowdsource:latest"

# curl as health check
echo "CHECKING AVAILABILITY OF LAUNCHED SERVICE (curl and expect http 200)..."

STATUS=1
for i in {1..15}
do
 REQUEST_RESULT=`curl --max-time 1 -s -i http://$HOST_IP:${it.application.port} | grep "200 OK"`

 if [ "$REQUEST_RESULT" != "" ]; then
  echo "REQUEST TO SERVICE SUCCESSFULLY RETURNED WITH CODE 200"
  STATUS=0
  break;
 else
  echo "RETURN CODE FROM SERVICE INCORRECT - WAITING..."
 fi
 sleep 2s
done

exit $STATUS