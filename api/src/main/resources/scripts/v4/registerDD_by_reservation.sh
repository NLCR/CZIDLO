#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials ../credentials/localhost/default

URL="https://${HOST}/api/v4/registrars/${REGISTRAR_CODE}/digitalDocuments"
METHOD=POST
INPUT_FILE=data/registerDD-by_reservation-v4.xml

echo 
echo "Registrace digitálního dokumentu (mód BY_RESERVATION - dříve rezervované urn:nbn ve vstupních datech) - API V4"
echo "=============================================================================================================="
echo "$METHOD $URL"
echo
curl -i --location --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP -X ${METHOD} -d @$INPUT_FILE -H 'Content-type: application/xml' $URL >$BODY_TMP
echo

print_headers
print_body
