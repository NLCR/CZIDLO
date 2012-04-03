#!/bin/bash
#Tento skript slouzi k otestovani importu pro priklady importnich xml
HEADERS_TMP=/tmp/headers
BODY_TMP=/tmp/body;

function import {
INPUT_FILE=$1
echo 
echo "Import dat ze souboru $INPUT_FILE"
echo "===================================================================================="
curl --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $HTTP_METHOD -d @$INPUT_FILE -H 'Content-type: application/xml' >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
}

URL=http://localhost:8080/api/v2/registrars/tst002/digitalDocuments
HTTP_METHOD=POST
LOGIN=mzkAdmin
PASSWORD=mzkAdminPass

#adresar importRecords je soucasti modulu foxml v adresari classes
FILE_DIR=/dir/with/importRecords

import $FILE_DIR/monograph.xml
import $FILE_DIR/monograph-withUrnNbn.xml
import $FILE_DIR/monographVolume.xml
import $FILE_DIR/periodical.xml
import $FILE_DIR/periodicalVolume.xml
import $FILE_DIR/periodicalIssue.xml
import $FILE_DIR/analytical.xml
import $FILE_DIR/thesis.xml
import $FILE_DIR/otherEntity-map.xml
import $FILE_DIR/otherEntity-musicSheet.xml
