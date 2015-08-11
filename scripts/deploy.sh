#!/bin/bash
# Script for simple webapps deployment
# Usage:
# redeploy.sh MODULE_NAME or deploy.sh all

VERSION=4.3.alpha
TOMCAT_HOME=/home/martin/software/apache-tomcat-8.0.24
CZIDLO_ROOT=/home/martin/git/CZIDLO

function deploy {
	APP=$1
	echo "deploying $APP-$VERSION.war"
	FROM=$CZIDLO_ROOT/$APP/target/$APP-$VERSION.war
	TO=$TOMCAT_HOME/webapps/$APP.war
	#echo "$FROM -> $TO"
	cp $FROM $TO
}


APP=$1
if [ $APP == all ] ; then
	deploy api
	deploy web
	deploy oaiPmhProvider
	deploy processDataServer
elif [ $APP == api ] || [ $APP == web ] || [ $APP == oaiPmhProvider ] || [ $APP == processDataServer ] ; then
	deploy $APP
fi

