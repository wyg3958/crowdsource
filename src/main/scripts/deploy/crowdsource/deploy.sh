#!/bin/bash

# Via SCP uploads the crowdfunding.service file to the given machine.
# Via SSH runs the crowdfunding container on that machine.
# Checks for availability of the crowdfunding app (fleet & HTTP).

if [ $# -ne 2 ]; then
	echo "usage: deploy {path to coreos_rsa} {AWS server URL}"
	exit
fi 


echo "UPLOADING .service FILE..."
scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $1/coreos_rsa $1/crowdsource/crowdfunding.service core@$2:/home/core

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
for i in {1..30}
do
 REQUEST_RESULT=`curl --max-time 1 -I -vvv $2:8080 | grep --color=never "200 OK"`
 
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
