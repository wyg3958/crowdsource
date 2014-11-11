#!/bin/bash
docker run -p ${it.application.port}:8080 -d --name="crowdsource" "asideas/crowdsource:latest"
# wait a few seconds for app to boot

# curl as health check
echo "CHECKING AVAILABILITY OF LOCALLY LAUNCHED SERVICE..."

STATUS=1
for i in {1..30}
do
 REQUEST_RESULT=`curl -sI http://localhost:${it.application.port} | grep "200 OK"`

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