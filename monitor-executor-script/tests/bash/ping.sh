#!/bin/bash

while [ $# -gt 0 ]
do
    case $1 in
    -pidFile) pidFile="$2" ; shift;;
    -url) url="$2" ; shift;;
    (-*) echo "$0: error - unrecognized option $1" 1>&2; exit 1;;
    (*) break;;
    esac
    shift
done

#TODO: map ping response time to TestExecution.result
error=$(ping -c1 $url 2>&1 >/dev/null)
returncode=$?
echo $returncode
if [ $returncode -eq 0 ]; then
    echo '{"testResult": "SUCCESS", "errors": []}' > $pidFile
elif [ $returncode -eq 1 ]; then
    echo '{"testResult": "ERROR", "errors": ["'$error'"]}' > $pidFile
else
    echo '{"testResult": "FAILED", "errors": []}' > $pidFile
fi