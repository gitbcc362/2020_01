#!/usr/bin/env bash
echo "**************************************************"
echo "Starting the replica set 2"
echo "**************************************************"

sleep 15 | echo Sleeping
mongo mongodb://mongo-rs0-1:27017 replicaSet.js