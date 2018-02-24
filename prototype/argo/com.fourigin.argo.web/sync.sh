#!/bin/bash
APPName="argo"
APPVersion="1.0-SNAPSHOT"
JARFile="$APPName-$APPVersion.jar"
SERVER="fourigin.de"
TARGETPath="/var/www/vhosts/fourigin.com/cms/"

rsync -avz build/libs/$JARFile $SERVER:$TARGETPath

exit 0