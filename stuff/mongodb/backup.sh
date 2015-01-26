KEYFILE=coreos_rsa
HOST=54.194.152.80
DUMPFILE=mongodump.tar.gz

ssh -t -i ${KEYFILE} core@${HOST} "
    docker run -it --rm -v /tmp/mongodump:/data/dump mongo:2.6 mongodump --host=172.17.42.1 --db=crowdsource --username=crowdsource --password='sRBfksXzltBgYHxvNUMoSKVHNLsIHHlI1B5Np2S8oyE=' --out=/data/dump
    tar czf /tmp/${DUMPFILE} -C /tmp mongodump
"

scp -i ${KEYFILE} core@${HOST}:/tmp/${DUMPFILE} .
git add ${DUMPFILE}
git commit -m "automatic backup of production database"
