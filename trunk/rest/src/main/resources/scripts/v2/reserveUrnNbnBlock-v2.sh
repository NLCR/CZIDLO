#!/bin/bash
source ../inc.sh

init_tmp_files
define_creditentials /home/martin/secret/resolver/nkpAdmin

REGISTRAR_CODE=tst02
SIZE=10

URL="https://$HOST/api/v2/registrars/$REGISTRAR_CODE/urnNbnReservations?size=$SIZE"
METHOD=POST

echo
echo "Rezervace balíku ${SIZE} URN:NBN pro registrátora ${REGISTRAR_CODE} - API V2"
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
