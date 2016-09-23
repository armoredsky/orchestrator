#!/usr/bin/env bash

# Required env variables:
#   $RANCHER_URL
#   $RANCHER_ACCESS_KEY - authenticates into the environment for deployment
#   $RANCHER_SECRET_KEY - authenticates into the environment for deployment
#   $CI_PIPELINE_ID - id of the pipeline to deploy (Also used in docker-compose file)
#   $IIB_URL - url for IIB

set -ev

rancher-compose --file docker-compose.yml up --upgrade --pull -d 
rancher-compose --file docker-compose.yml up --upgrade --confirm-upgrade -d