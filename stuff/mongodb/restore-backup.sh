#!/bin/bash

# Restores the mongodump backup

KEYFILE=coreos_rsa
HOST=54.194.152.80
DUMPFILE=mongodump.tar.gz

scp -i ${KEYFILE} ${DUMPFILE} core@${HOST}:/tmp/

ssh -i ${KEYFILE} core@${HOST} "
    rm -rf /tmp/mongorestore
    mkdir /tmp/mongorestore
    tar xzf /tmp/${DUMPFILE} -C /tmp/mongorestore

    docker run --rm -v /tmp/mongorestore:/data/dump mongo:2.6 mongorestore --host=172.17.42.1 --db=crowdsource --username=crowdsource --password='sRBfksXzltBgYHxvNUMoSKVHNLsIHHlI1B5Np2S8oyE=' /data/dump/mongodump/crowdsource
"
