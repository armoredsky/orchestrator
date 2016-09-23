#!/usr/bin/env bash

# Required env variables:
#   $CI_USER_PASSWORD - login password of the CI user
#   $CI_PIPELINE_ID - id of the pipeline

# Arguments
#   1: docker-compose service name

service=$1

set -ev

# Ensure cleanup
function cleanup() {

  # Ensure graceful exit from cleanup
  set +e
  trap - EXIT

  # Stop and remove containers and images
  docker-compose down -v --rmi=all

  exit
}

trap cleanup EXIT SIGINT SIGHUP SIGTERM SIGSTOP

# Build service
docker-compose --file docker-compose.dev.yml build ${service}
docker tag uomdesktop_${service} registry.gitlab.com/uom-desktop/${service}:$CI_PIPELINE_ID

# Push image to Gitlab registry
docker login -u uomlocal-ci -p $CI_USER_PASSWORD registry.gitlab.com
docker push registry.gitlab.com/uom-desktop/${service}:$CI_PIPELINE_ID

# Clean up image
docker rmi registry.gitlab.com/uom-desktop/${service}:$CI_PIPELINE_ID