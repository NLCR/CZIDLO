#!/bin/bash
HEADERS_TMP=headers
BODY_TMP=body;

DIG_INST_ID=4
TITLE="Deaktivacer digitalni instance s id $DIG_INST_ID"
URL=https://localhost/api/v3/digitalInstances/id/$DIG_INST_ID
HTTP_METHOD=DELETE

LOGIN=nkpAdmin
PASSWORD=nkpAdminPass

echo
echo $TITLE
echo "==================================="
curl --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $HTTP_METHOD  >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
