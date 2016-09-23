#!/bin/bash

flyway -url=jdbc:postgresql://database/${DATABASE} -user=${DATABASE_USER} -schemas=uom migrate