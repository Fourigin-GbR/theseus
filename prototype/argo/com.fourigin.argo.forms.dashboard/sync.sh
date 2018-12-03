#!/bin/bash
APPRoot="/var/www/vhosts/fourigin.com/apps"
APPName="argo-forms-dashboard"
APPVersion="1.0-SNAPSHOT"
JARFile="$APPName-$APPVersion.jar"
SERVER="fourigin.de"

mode=$1
case "$mode" in
dev)
    echo "Synchronizing with DEV environment"
    TARGETPath="$APPRoot/dev-forms-dashboard"
    ;;
stage)
    echo "Synchronizing with STAGE environment"
    TARGETPath="$APPRoot/forms-dashboard"
    ;;
*)
    echo "Use 'dev' or 'stage' as a command line parameter, to define the sync target"
    exit 0;
esac

rsync -avz build/libs/$JARFile $SERVER:$TARGETPath

exit 0