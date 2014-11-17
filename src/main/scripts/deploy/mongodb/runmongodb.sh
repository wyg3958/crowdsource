#!/bin/bash

# Via SSH runs on the given machine a MongoDB database container that expects an exposed storage folder from 'mongodbdata' container.
#
# Run this script AFTER running the MongoDB persistence container.

if [ $# -ne 2 ]; then
	echo "usage: runmongodb {path to coreos_rsa} {AWS server URL}"
	exit
fi 


echo "ACCESSING AWS..."
ssh -i $1/coreos_rsa core@$2 "

 echo "AWS - STOPPING MONGODB CONTAINER..."
 docker stop mongodb

 echo "AWS - REMOVING MONGODB CONTAINER..."
 docker rm mongodb

 echo "AWS - DELETING MONGODB CONTAINER..."
 docker rmi mongodb

 echo "AWS - RUNNING MONGODB CONTAINER..."
 docker run -d -p 27017:27017 --volumes-from mongodbdata --name mongodb dockerfile/mongodb

 echo "AWS - LEAVING AWS..."
 exit \$STATUS"

echo "ALL DONE"

