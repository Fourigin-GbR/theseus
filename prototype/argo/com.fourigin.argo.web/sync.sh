#!/bin/bash
APPRoot="/var/www/vhosts/fourigin.com/apps"
APPName="argo"
APPVersion="1.0-SNAPSHOT"
JARFile="$APPName-$APPVersion.jar"
SERVER="fourigin.de"
TARGETPath="$APPRoot/argo"

rsync -avz build/libs/$JARFile $SERVER:$TARGETPath

exit 0