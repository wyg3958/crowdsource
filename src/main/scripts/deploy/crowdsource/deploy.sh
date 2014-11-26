#!/bin/bash

# Via SCP uploads the crowdsource.service file to the given machine.
# Via SSH runs the crowdsource container on that machine.
# Checks for availability of the crowdsource app (fleet & HTTP).

if [ $# -ne 2 ]; then
	echo "usage: deploy {path to coreos_rsa} {AWS server URL}"
	exit
fi 


echo "UPLOADING .service FILE..."
scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $1/coreos_rsa $1/crowdsource/crowdsource.service core@$2:/home/core

echo "UPLOADING deploy script..."
scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $1/coreos_rsa $1/crowdsource/instructions.sh core@$2:/home/core

echo "UPLOADS DONE - ACCESSING AWS..."

ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $1/coreos_rsa core@$2 "bash /home/core/instructions.sh"

if [ $? -ne	0 ]; then
 echo "!!! AWS ACTION NOT SUCCESSFUL - EXITING !!!"	
 exit 1
fi

STATUS=1

echo "CHECKING AVAILABILITY OF AWS SERVICE..."

for i in {1..15}
do
 REQUEST_RESULT=`curl --max-time 30 -I -s $2:80 | grep "200 OK"`

 if [ "$REQUEST_RESULT" != "" ]; then
  echo "REQUEST TO SERVICE SUCCESSFULLY RETURNED WITH CODE 200"
  STATUS=0
  break;
 else
  echo "RETURN CODE FROM SERVICE INCORRECT - WAITING..."
 fi
 sleep 5s
done

if [ $STATUS -ne 0 ]; then
 echo "!!! REQUEST TO AWS DID NOT RETURN CORRECTLY !!!"	
 exit 1
fi
