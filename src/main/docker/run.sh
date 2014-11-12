#!/bin/bash
docker -H tcp://10.1.42.1:2375 run -p ${it.application.port}:8080 -d --name="crowdsource" "asideas/crowdsource:latest"
# wait a few seconds for app to boot

# get host ip
HOST_IP=`netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}'`

# curl as health check
EXECUTION_STRING="curl -I http://$HOST_IP:${it.application.port} | grep \"200 OK\""
echo "CHECKING AVAILABILITY OF LAUNCHED SERVICE ($EXECUTION_STRING)..."

STATUS=1
for i in {1..60}
do
 REQUEST_RESULT=`curl -s -i http://$HOST_IP:9999 | grep "200 OK"`

 if [ "$REQUEST_RESULT" != "" ]; then
  echo "REQUEST TO SERVICE SUCCESSFULLY RETURNED WITH CODE 200"
  STATUS=0
  break;
 else
  echo "RETURN CODE FROM SERVICE INCORRECT - WAITING..."
 fi
 sleep 1
done

exit $STATUS