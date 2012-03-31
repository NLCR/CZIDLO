#!/bin/bash
HEADERS_TMP=/tmp/headers
BODY_TMP=/tmp/body

TITLE="Vložení nové digitální instance"
URN_NBN=urn:nbn:cz:tst02-000001
URL=https://localhost/api/v2/resolver/$URN_NBN/digitalInstances
HTTP_METHOD=POST
LOGIN=mzkAdmin
PASSWORD=mzkAdminPass
INPUT_FILE=/home/martin/Dropbox/Resolver/REST-test/addDigitalInstance.xml


echo
echo $TITLE
echo "==================================="

curl --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $HTTP_METHOD -d @$INPUT_FILE -H 'Content-type: application/xml' >$BODY_TMP

#cat $BODY | curl --insecure --basic --user $LOGIN:$PASSWORD -i -D $HEADERS_TMP -X POST -H 'Content-type: application/xml' -d @- https://localhost/api/v2/resolver/urn:nbn:cz:tst02-000001/digitalInstanc$
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
