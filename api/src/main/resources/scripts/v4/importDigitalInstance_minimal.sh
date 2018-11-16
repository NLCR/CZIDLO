#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials ../credentials/localhost/default

URN_NBN=urn:nbn:cz:tst02-000001
URL=https://${HOST}/api/v4/resolver/${URN_NBN}/digitalInstances

METHOD=POST
INPUT_FILE=data/importDitalInstance_minimal-v4.xml

echo
echo "Import digitální instance - API V4"
echo "=================================="
echo "$METHOD $URL"
echo
curl -i --location --insecure --basic --user ${LOGIN}:${PASSWORD} -D ${HEADERS_TMP} -X ${METHOD} -d @${INPUT_FILE} -H 'Content-type: application/xml' ${URL}
echo

print_headers
print_body
