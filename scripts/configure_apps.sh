#!/bin/bash
# Script for replacing modules' configuration from backup
# after some apps are redeployed.

BAK_DIR=/home/martin/software/apache-tomcat-8.0.24/bak
TOMCAT_HOME=/home/martin/software/apache-tomcat-8.0.24

function replace_prop {
	APP=$1
	echo "$APP: updating configuration properties"
	FILE_BAK=$BAK_DIR/$APP.properties
	FILE_DEPLOYED=$TOMCAT_HOME/webapps/$APP/WEB-INF/classes/$APP.properties
	cp $FILE_BAK $FILE_DEPLOYED
}

function replace_context {
	APP=$1
	echo "$APP: updating context"
	FILE_BAK=$BAK_DIR/$APP.xml
	FILE_DEPLOYED=$TOMCAT_HOME/conf/Catalina/localhost/$APP.xml
	cp $FILE_BAK $FILE_DEPLOYED
}

function replace_hibernate_conf {
	APP=$1
        echo "$APP: updating hibernate.conf"
        FILE_BAK=$BAK_DIR/$APP-hibernate.cfg.xml
        FILE_DEPLOYED=$TOMCAT_HOME/webapps/$APP/WEB-INF/classes/hibernate.cfg.xml
        cp $FILE_BAK $FILE_DEPLOYED
}

echo "Replacing configuration from backup:"

replace_prop web
replace_context web
replace_hibernate_conf web

replace_prop processDataServer
replace_context processDataServer
replace_hibernate_conf processDataServer

replace_prop api
replace_context api

replace_prop oaiPmhProvider
replace_context oaiPmhProvider


