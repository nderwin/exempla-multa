#!/bin/bash

set -e
set -u

psql -v ON_ERROR_STOP=1 -U postgres <<-EOSQL
    CREATE DATABASE quarkus WITH OWNER = postgres;
    CREATE DATABASE keycloak WITH OWNER = postgres;
    ALTER USER "postgres" WITH PASSWORD 'postgres';
EOSQL
