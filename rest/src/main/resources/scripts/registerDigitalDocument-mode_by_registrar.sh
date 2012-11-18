#!/bin/bash
HEADERS_TMP=/tmp/import-headers
BODY_TMP=/tmp/import-body;

rm $HEADERS_TMP
rm $BODY_TMP

TITLE="Registrace digitálního dokumentu (mód BY_REGISTRAR - urn je přítomno ve vstupním xml)"
INPUT_FILE=data/importWithUrnNbn.xml

HOST=localhost
LOGIN=nkpAdmin  
PASSWORD=nkpAdminPass
REGISTRAR_CODE=tst02

URL=https://$HOST/api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments

echo 
echo $TITLE
echo "==================================="
curl -i --location --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP -X POST -d @$INPUT_FILE -H 'Content-type: application/xml' $URL >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
