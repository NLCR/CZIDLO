#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials ../credentials/localhost/default

URN_NBN=urn:nbn:cz:tst02-000003
URL="https://${HOST}/api/v3/resolver/${URN_NBN}/registrarScopeIdentifiers/"
METHOD=DELETE

echo 
echo "Odstranění všech registrar-scope identifikátorů digitálního dokumentu - API V3"
echo "=============================================================================="
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
