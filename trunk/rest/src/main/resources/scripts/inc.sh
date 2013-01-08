#!/bin/bash

function init_tmp_files() {
HEADERS_TMP=/tmp/import-headers
BODY_TMP=/tmp/import-body;

rm $HEADERS_TMP
rm $BODY_TMP
}


function define_creditentials() {
HOST=`cat ${1}/host`;
LOGIN=`cat ${1}/login`;
PASSWORD=`cat ${1}/password`;
REGISTRAR_CODE=`cat ${1}/registrarCode`;
}

function print_headers() {
echo "Hlavicky"
echo "--------"
cat $HEADERS_TMP
echo
}

function print_body() {
echo "Telo"
echo "--------"
cat $BODY_TMP | xmlindent
}
