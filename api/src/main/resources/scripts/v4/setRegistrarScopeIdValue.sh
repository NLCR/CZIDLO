#!/bin/bash
source ../inc.sh

init_tmp_files
define_credentials /home/martin/secret/resolver-test1/mzk-rehan

URNNBN=urn:nbn:cz:tst02-000001
ID_TYPE=someIdType2
ID_VALUE=newNewValue
URL="https://${HOST}/api/v4/resolver/${URNNBN}/registrarScopeIdentifiers/$ID_TYPE"
METHOD=PUT

echo
echo "Nastavení/aktualizace hodnoty registrar-scope identifikátoru - API V4"
echo "====================================================================="
echo $ID_VALUE | curl -i --location --insecure  --basic --user $LOGIN:$PASSWORD -D $HEADERS_TMP -X $METHOD  -d @- $URL >$BODY_TMP
echo

echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo

echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
