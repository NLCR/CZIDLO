#!/bin/bash

HEADERS_TMP=/tmp/headers
BODY_TMP=/tmp/body.out

METHOD=PUT
LOGIN=mzkAdmin
PASSWORD=mzkAdminPass
URNNBN=urn:nbn:cz:tst002-000001
ID_TYPE=someIdType
ID_VALUE=newValue
URL=http://localhost:8080/api/v2/resolver/$URNNBN/identifiers/$ID_TYPE

echo
echo "Vložení/aktualizace indentifkátoru digitálního dokument v rámci registrátora"
echo "============================================================================="
echo $ID_VALUE | curl --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP -X $METHOD -H 'Content-type: application/xml' -d @- $URL >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
