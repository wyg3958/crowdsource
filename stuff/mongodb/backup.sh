#!/bin/bash

# The jenkins job is configured to clone the crowdsource git repository (name = "origin") and executes the following shell command:
#
# cd stuff/mongodb
# chmod 400 coreos_rsa
# ./backup.sh
#
# Then the "GitPublisher" post-build action pushes the branch "master" to the target remote name "origin".

KEYFILE=coreos_rsa
HOST=51.194.152.80
DUMPFILE=mongodump.tar.gz

ssh -i ${KEYFILE} core@${HOST} "
    docker run --rm -v /tmp/mongodump:/data/dump mongo:2.6 mongodump --host=172.17.42.1 --db=crowdsource --username=crowdsource --password='sRBfksXzltBgYHxvNUMoSKVHNLsIHHlI1B5Np2S8oyE=' --out=/data/dump
    tar czf /tmp/${DUMPFILE} -C /tmp mongodump
"

scp -i ${KEYFILE} core@${HOST}:/tmp/${DUMPFILE} .
git add ${DUMPFILE}
git commit -m "automatic backup of production database"
