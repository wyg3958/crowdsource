#!/bin/bash

# Connects to the given machine via SSH, clones the github repository auth0/coreos-mongodb.git and starts the mongo and mongo-replica-config service from this project

if [ $# -ne 2 ]; then
	echo "usage: $0 {path to coreos_rsa} {AWS server URL}"
	exit
fi


ssh -i $1 core@$2 "

    mkdir -p services
    cd services

    git clone https://github.com/auth0/coreos-mongodb.git

    cd coreos-mongodb

    etcdctl set /mongo/replica/name crowdsourcereplica

    echo 'Starting services'
    fleetctl start mongo@{1..3}.service mongo-replica-config.service
"

echo "Services are starting. Once they are all up, execute the following commands on one node:"

echo "
#################################################################################################################################################

SITE_USER_PWD=\$(etcdctl get /mongo/replica/siteUserAdmin/pwd)
REPLICA=\$(etcdctl get /mongo/replica/name)
FIRST_NODE=\$(fleetctl list-machines --no-legend | awk '{print \$2}' | head -n 1)

docker run -it --rm mongo:2.6 mongo \$REPLICA/\$FIRST_NODE/admin -u siteUserAdmin -p \$SITE_USER_PWD

use crowdsource
db.createUser({user: \"crowdsource2\", pwd: \"sRBfksXzltBgYHxvNUMoSKVHNLsIHHlI1B5Np2S8oyE=\", roles: [{role: \"dbOwner\", db: \"crowdsource\"}]})

#################################################################################################################################################
"
