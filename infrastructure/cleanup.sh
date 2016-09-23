#!/usr/bin/env bash

set -v

# Remove any extra docker images
docker rmi $(docker images -f "dangling=true" -q)

# Ensure successful exit whether anything was removed or not
exit 0