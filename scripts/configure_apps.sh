#!/bin/bash
# Script for replacing modules' configuration from backup
# after some apps are redeployed.

BAK_DIR=/home/martin/software/apache-tomcat-8.0.24/bak/
TOMCAT_HOME=/home/martin/software/apache-tomcat-8.0.24

function replace_prop {
	APP=$1
	PROP_BAK=$BAK_DIR/$APP.properties
	PROP_DEPLOYED=$TOMCAT_HOME/webapps/$APP/WEB-INF/classes/$APP.properties
	echo "loading configuration properties for app $MODULE"
	cp $PROP_BAK $PROP_DEPLOYED
}

echo "Replacing configuration properties from backup:"
replace_prop web
replace_prop api
replace_prop processDataServer
replace_prop oaiPmhProvider

#TODO: update context from backup
