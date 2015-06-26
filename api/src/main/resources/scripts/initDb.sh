#!/bin/bash
#Tento skript slouzi k odstaraneni a novemu vytvoreni databaze resolveru
#Pro vytvoreni 
#soubor initDb-postgresql.sql je soucasti modululu persistence v adresari classes
SQL_SCRIPT=/path/to/file/initDb-postgresql.sql
HOST=localhost
PORT=5432
DATABASE=resolver
LOGIN=testuser
PASSWORD=testpass

echo "Inicializace databáze"
echo "====================="
export PGPASSWORD=$PASSWORD
psql --no-password -U $LOGIN -d $DATABASE -h $HOST -f $SQL_SCRIPT
