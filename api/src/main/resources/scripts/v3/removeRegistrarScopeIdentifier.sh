#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials /home/martin/secret/resolver/nkpAdmin

URN_NBN=urn:nbn:cz:tst02-000001
ID_TYPE=signatura
URL="https://${HOST}/api/v3/resolver/${URN_NBN}/registrarScopeIdentifiers/${ID_TYPE}"
METHOD=DELETE

echo
echo "Odstranění registrar-scope identifikátoru určeného typu - API V3"
echo "================================================================"
echo
echo "${METHOD} ${URL}"
curl --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $METHOD   >$BODY_TMP
#curl -D $HEADERS_TMP $URL -X $HTTP_METHOD  >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
