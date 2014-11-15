#!/bin/bash

# Via SSH runs on the given machine a MongoDB docker container with the persistence folder of MongoDB.
# This folder then can be linked by a MongoDB database container as external storage.
#
# Run this script BEFORE running the MongoDB database container.
#
# WARNING: An already existing persistence container will be removed!

if [ $# -ne 2 ]; then
	echo "usage: createmongodbdata {path to coreos_rsa} {AWS server URL}"
	exit
fi 


echo "ACCESSING AWS..."
ssh -i $1/coreos_rsa core@$2 "

 echo "AWS - STOPPING MONGODBDATA CONTAINER..."
 docker stop mongodbdata

 echo "AWS - REMOVING MONGODBDATA CONTAINER..."
 docker rm mongodbdata

 echo "AWS - DELETING MONGODBDATA CONTAINER..."
 docker rmi mongodbdata

 echo "AWS - RUNNING MONGODBDATA CONTAINER..."
 docker run -d  -v /data/db --name mongodbdata dockerfile/mongodb echo true

 echo "AWS - LEAVING AWS..."
 exit \$STATUS"

echo "ALL DONE"

