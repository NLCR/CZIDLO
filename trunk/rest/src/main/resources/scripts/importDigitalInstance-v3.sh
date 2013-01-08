#!/bin/bash
source inc.sh

init_tmp_files
define_creditentials /home/martin/secret/resolver/nkpAdmin

URN_NBN=urn:nbn:cz:tst02-000062
URL=https://${HOST}/api/v3/resolver/${URN_NBN}/digitalInstances

METHOD=POST
INPUT_FILE=data/importDitalInstance_complete-v3.xml

echo 
echo "Registrace digitálního dokumentu (mód BY_REGISTRAR - urn je přítomno ve vstupním xml) - API V2"
echo "=============================================================================================="
echo "$METHOD $URL"
echo
curl -i --location --insecure --basic --user ${LOGIN}:${PASSWORD} -D ${HEADERS_TMP} -X ${METHOD} -d @${INPUT_FILE} -H 'Content-type: application/xml' ${URL} 
echo

print_headers
print_body
