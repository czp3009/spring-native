#!/usr/bin/env bash
RESPONSE=`curl -s localhost:8080`
if [[ $RESPONSE == *"Welcome"* ]]; then
 exit 0
else
 exit 1
fi
