#!/bin/bash

function init_tmp_files() {
HEADERS_TMP=/tmp/import-headers
BODY_TMP=/tmp/import-body;

rm $HEADERS_TMP
rm $BODY_TMP
}

#autentizace při použití skriptů: prvni parametr funkce define_creditential() obsahuje název adresáře
#tento adresář obsahuje následující soubory:
#host - obsahuje doménu (případně port) stroje, na kterém běží czidlo
#login - přihlašovací jméno
#password - heslo
#registrarCode - kód registrátora

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
