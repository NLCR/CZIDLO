#!/bin/bash
source inc.sh

init_tmp_files
define_creditentials /home/martin/secret/resolver/nkpAdmin

URL="https://${HOST}/api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments"
METHOD=POST
INPUT_FILE=data/registerDD-by_resolver-v3_with_predecessors.xml

echo 
echo "Registrace digitálního dokumentu (mód BY_RESOLVER - urn:nbn přiřadí Resolver a vrátí v odpovědi) - API V3"
echo "========================================================================================================="
echo "$METHOD $URL"
echo
curl -i --location --insecure --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP -X ${METHOD} -d @$INPUT_FILE -H 'Content-type: application/xml' $URL >$BODY_TMP
echo

print_headers
print_body
