#!/usr/bin/env bash

DATABASE_HOST="${1:-localhost}"

psql -h $DATABASE_HOST -U uom uom -f src/main/resources/db/seed/delete-sample-data.sql
psql -h $DATABASE_HOST -U uom uom -f src/main/resources/db/seed/north-american-seafood-sample-data.sql
psql -h $DATABASE_HOST -U uom uom -f src/main/resources/db/seed/the-creek-deli-sample-data.sql
