#!/usr/bin/env bash

set -e

url=$1

echo -n "waiting for TCP connection to $host:$port..."

counter=0

until $(curl --output /dev/null --silent --head --fail ${url}); do
    printf '.'
    sleep 1

    if [ $counter -ge 30 ];then
        printf "\nPAGE NEVER STARTED!!!\n"
        exit 1
    fi
    counter=$(($counter+1))
done

echo 'ok'
exit 0