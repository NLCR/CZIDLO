#!/bin/bash
HEADERS_TMP=/tmp/headers
BODY_TMP=/tmp/body

TITLE="Vložení nové digitální instance"
URN_NBN=urn:nbn:cz:boa001-000001
LIBRARY_ID=2
URL="http://localhost:8080/api/v2/resolver/$URN_NBN/digitalInstances?libraryId=$LIBRARY_ID"
HTTP_METHOD=POST
DIG_INST_URL=http://kramerius.mzk.cz/search/handle/uuid:440a7579-9cb3-11e0-8637-0050569d679d
LOGIN=mzkAdmin
PASSWORD=mzkAdminPass


echo 
echo $TITLE
echo "==================================="
echo $DIG_INST_URL|curl --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $HTTP_METHOD -d @-  >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
