#!/bin/bash

# kill stuff (if exists)
docker kill crowdsource_test
docker rm crowdsource_test
docker kill crowdsourcetestdb
docker rm crowdsourcetestdb
docker kill crowdsourcetestmailserver
docker rm crowdsourcetestmailserver

# run mailserver
docker run -d -p 1025:1025 -p 18080:8080 --name crowdsourcetestmailserver gaffa/restmail

# run mongodb (non-default ports [would be 27017, 28017] to avoid problems)
docker run -d -p 27000:27017 -p 28000:28017 --name crowdsourcetestdb dockerfile/mongodb

# get host ip
HOST_IP=`netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}'`

# let db and mailserver get awake
sleep 5
#echo "waiting for mailserver to deliver http 200"
#for i in {1..30}
#do
# REQUEST_RESULT=`curl --max-time 1 -s -i http://$HOST_IP:18080/mails | grep "200 OK"`
#
# if [ "$REQUEST_RESULT" != "" ]; then
#  echo "REQUEST TO SERVICE SUCCESSFULLY RETURNED WITH CODE 200"
#  STATUS=0
#  break;
# else
#  echo "RETURN CODE FROM SERVICE INCORRECT - WAITING..."
# fi
# sleep 1s
#done


echo "outputting logs of mongodb-container"
docker logs crowdsourcetestdb

# run application
docker run -p ${it.application.port}:8080 -d \
 -e de.axelspringer.ideas.crowdsource.db.host=$HOST_IP \
 -e de.axelspringer.ideas.crowdsource.db.port=27000 \
 -e de.axelspringer.ideas.crowdsource.mail.host=$HOST_IP\
 -e de.axelspringer.ideas.crowdsource.mail.port=1025 \
 -e de.axelspringer.ideas.crowdsource.mail.starttls=false \
 -e de.axelspringer.ideas.crowdsource.baseUrl=http://$HOST_IP:${it.application.port} \
 --name="crowdsource_test" "asideas/crowdsource:latest"

# curl as health check
echo "CHECKING AVAILABILITY OF LAUNCHED SERVICE (curl and expect http 200)..."

STATUS=1
APP_URL=http://$HOST_IP:${it.application.port}
echo "connecting to: $APP_URL..."
for i in {1..30}
do
 REQUEST_RESULT=`curl --max-time 1 -s -i $APP_URL | grep "200 OK"`

 if [ "$REQUEST_RESULT" != "" ]; then
  echo "REQUEST TO SERVICE SUCCESSFULLY RETURNED WITH CODE 200"
  STATUS=0
  break;
 else
  echo "RETURN CODE FROM SERVICE INCORRECT - WAITING..."
 fi
 sleep 5s
done

if [ $STATUS != 0 ]; then
 echo "ERROR: SERVICE NOT AVAILABLE AFTER 30 ATTEMPTS! LOGS:"
 docker logs crowdsource_test
fi

exit $STATUS