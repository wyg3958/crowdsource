#!/bin/bash

# kill mongodb (if exists)
docker kill crowdsourcetestdb
docker rm crowdsourcetestdb
# run mongodb (non-default ports [would be 27017, 28017] to avoid problems)
docker run -d -p 27000:27017 -p 28000:28017 --name crowdsourcetestdb dockerfile/mongodb

# let db get awake
sleep 2
echo "outputting logs of mongodb-container"
docker logs crowdsourcetestdb

# get host ip
HOST_IP=`netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}'`

# run application
docker run -p ${it.application.port}:8080 -d -e de.axelspringer.ideas.crowdsource.db.host=$HOST_IP --name="crowdsource" "asideas/crowdsource:latest" -Dspring.profiles.active=ci

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

if [ $STATUS != 0 ]; then
 echo "ERROR: SERVICE NOT AVAILABLE AFTER 15 ATTEMPTS!"
fi

exit $STATUS