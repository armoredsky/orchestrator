#!/usr/bin/env bash

# Required env variables:
#   $CI_USER_PASSWORD - login password of the CI user
#   $CI_PIPELINE_ID - id of the pipeline to pull (Used in docker-compose file)
#   $IIB_URL - url for IIB or IIB stub

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

./infrastructure/pull-image.sh api database migrations

# Impose a strict order here
docker-compose up -d database
docker-compose up migrations # waits for the database to be up

docker-compose run --no-deps --rm -e IIB_URL=$IIB_URL -e DATABASE_HOST=database -e DATABASE=uom api functionalTest-ci --info