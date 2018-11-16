#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials /home/martin/secret/resolver/nkpAdmin

DIG_INST_ID=22
URL=https://${HOST}/api/v3/digitalInstances/id/${DIG_INST_ID}

METHOD=DELETE

echo
echo "Deaktivace digitální instance - API V3"
echo "======================================"
echo "$METHOD $URL"
echo
curl -i --location --insecure --basic --user ${LOGIN}:${PASSWORD} -D ${HEADERS_TMP} -X ${METHOD} -d @${INPUT_FILE} -H 'Content-type: application/xml' ${URL}
echo

print_headers
print_body
