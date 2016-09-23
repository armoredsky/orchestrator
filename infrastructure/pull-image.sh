#!/usr/bin/env bash

# Required env variables:
#   $CI_USER_PASSWORD - login password of the CI user
#   $CI_PIPELINE_ID - id of the pipeline

# Arguments
#   1: docker-compose service names

set -ev

services="$@"

for service in $services
do
    docker login -u uomlocal-ci -p $CI_USER_PASSWORD registry.gitlab.com
    docker pull registry.gitlab.com/uom-desktop/${service}:$CI_PIPELINE_ID
done