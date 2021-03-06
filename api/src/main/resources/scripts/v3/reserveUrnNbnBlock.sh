#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials ../credentials/localhost/default

REGISTRAR_CODE=tst02
#REGISTRAR_CODE=tst01
SIZE=10

URL="https://$HOST/api/v3/registrars/$REGISTRAR_CODE/urnNbnReservations?size=$SIZE"
METHOD=POST

echo
echo "Rezervace balíku ${SIZE} URN:NBN pro registrátora ${REGISTRAR_CODE} - API V3"
echo "============================================================================"
echo 
echo "${METHOD} ${URL}"
curl --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $METHOD >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
