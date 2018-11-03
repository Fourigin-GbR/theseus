#!/bin/bash
APPName="argo-forms-dashboard"
APPVersion="1.0-SNAPSHOT"
JARFile="$APPName-$APPVersion.jar"
SERVER="fourigin.de"
TARGETPath="/var/www/vhosts/fourigin.com/forms-dashboard"

rsync -avz build/libs/$JARFile $SERVER:$TARGETPath

exit 0