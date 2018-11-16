#!/bin/bash
source ../inc.sh

init_tmp_files
define_creditentials /home/martin/secret/resolver/nkpAdmin

URNNBN=urn:nbn:cz:tst02-000001
ID_TYPE=something
ID_VALUE=newNewValue2
URL="https://${HOST}/api/v2/resolver/${URNNBN}/identifiers/$ID_TYPE"
METHOD=PUT

echo
echo "Nastavení/aktualizace hodnoty registrar-scope identifikátoru - API V2"
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
