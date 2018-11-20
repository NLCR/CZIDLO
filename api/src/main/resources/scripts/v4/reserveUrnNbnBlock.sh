#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials ../credentials/localhost/default

REGISTRAR_CODE=tst01
SIZE=3

URL="https://$HOST/api/v4/registrars/$REGISTRAR_CODE/urnNbnReservations?size=$SIZE"
METHOD=POST

echo
echo "Rezervace balíku ${SIZE} URN:NBN pro registrátora ${REGISTRAR_CODE} - API V4"
echo "============================================================================"
echo
echo "${METHOD} ${URL}"

CMD="curl --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP $URL -X $METHOD"
echo $CMD
$CMD >$BODY_TMP
echo

print_headers
print_body

#echo
#cat $BODY_TMP
