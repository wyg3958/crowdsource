#!/bin/bash
set -x
if [ $# -ne 2 ]; then
	echo "usage: deploy {path to coreos_rsa} {AWS server URL}"
	exit
fi 
hostname -i
pwd
ls -al $1

echo "UPLOADING .service FILE..."
scp -i $1/coreos_rsa -vvv $1/crowdfunding.service core@$2:/home/core;
echo "UPLOAD DONE - ACCESSING AWS..."
ssh -i $1/coreos_rsa core@$2 "

 echo "AWS - MOVING SERVICE FILE..."
 sudo mv crowdfunding.service /etc/systemd/system

 echo "AWS - STOPPING crowdfunding SERVICE..."
 fleetctl stop crowdfunding

 echo "AWS - DESTROYING crowdfunding IMAGE..."
 fleetctl destroy crowdfunding

 echo "AWS - LOGGING IN TO DOCKER HUB..."
 docker login -e crowdsource@asideas.de -p ideas987 -u asjenkins

 echo "AWS - PULLING crowdfunding IMAGE..."
 docker pull asideas/crowdsource;

 echo "AWS - STARTING crowdfunding SERVICE VIA FLEET..."
 fleetctl start /etc/systemd/system/crowdfunding.service

 STATUS=1

 echo "AWS - CHECKING THAT SERVICE IS RUNNING..."
 for i in {1..15}
 do
  SERVICE_RESULT=\`fleetctl list-units | grep crowdfunding\\.service.*active.*running\`
 
  if [ \"\$SERVICE_RESULT\" == \"\" ]; then
   echo "AWS - RUNNING SERVICE NOT DETECTED - WAITING..."
  else
   echo "AWS - RUNNING SERVICE FOUND"
   STATUS=0
   break
  fi
  sleep 1
 done

 echo "AWS - LEAVING AWS..."
 exit \$STATUS"

if [ $? -ne	0 ]; then
 echo "!!! AWS ACTION NOT SUCCESSFUL - EXITING !!!"	
 exit
fi

STATUS=1

echo "CHECKING AVAILABILITY OF AWS SERVICE..." 
for i in {1..30}
do
 REQUEST_RESULT=`curl -s -I $2:8080 | grep "200 OK"`
 
 if [ "$REQUEST_RESULT" != "" ]; then
  echo "REQUEST TO SERVICE ON AWS SUCCESSFULLY RETURNED WITH CODE 200"
  STATUS=0
  break;
 else
  echo "RETURN CODE FROM AWS SERVICE INCORRECT - WAITING..."
 fi
 sleep 1
done

echo "ALL DONE"

if [ $STATUS -ne 0 ]; then
 echo "!!! REQUEST TO AWS DID NOT RETURN CORRECTLY !!!"	
 exit 1
fi
