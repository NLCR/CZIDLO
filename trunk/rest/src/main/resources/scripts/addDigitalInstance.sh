#!/bin/bash
HEADERS_TMP=/tmp/headers
BODY_TMP=/tmp/body

URN_NBN=urn:nbn:cz:tst02-000001
URL=https://localhost/api/v3/resolver/$URN_NBN/digitalInstances
HTTP_METHOD=POST
LOGIN=mzkAdmin
PASSWORD=mzkAdminPass
INPUT_FILE=/home/martin/Dropbox/Resolver/REST-test/addDigitalInstance.xml


echo
echo "Vložení nové digitální instance"
echo "==============================="
curl --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $HTTP_METHOD -d @$INPUT_FILE -H 'Content-type: application/xml' >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
