#!/bin/bash

set -euo pipefail

REQUEST_FILE="/tmp/rejoin-as-standby.conf"
PGDATA="${PGDATA:-/var/lib/postgresql/data}"

if [[ -f "$REQUEST_FILE" ]]; then
    # shellcheck disable=SC1090
    source "$REQUEST_FILE"
    SOURCE_HOST="${SOURCE_HOST:?missing SOURCE_HOST in $REQUEST_FILE}"
    SOURCE_PORT="${SOURCE_PORT:-5432}"

    echo "Rejoining this node as standby from $SOURCE_HOST:$SOURCE_PORT"
    rm -f "$PGDATA/fill_disk"
    find "$PGDATA" -mindepth 1 -maxdepth 1 -exec rm -rf {} +
    chown -R postgres:postgres "$PGDATA"

    until pg_isready -h "$SOURCE_HOST" -p "$SOURCE_PORT" -U postgres; do
        echo "Waiting for source server $SOURCE_HOST:$SOURCE_PORT..."
        sleep 2
    done

    if [[ "$(id -u)" = "0" ]]; then
        gosu postgres env PGPASSWORD='replicator_password' pg_basebackup \
            -h "$SOURCE_HOST" \
            -p "$SOURCE_PORT" \
            -D "$PGDATA" \
            -U replicator \
            -P \
            -X stream \
            -R
    else
        PGPASSWORD='replicator_password' pg_basebackup \
            -h "$SOURCE_HOST" \
            -p "$SOURCE_PORT" \
            -D "$PGDATA" \
            -U replicator \
            -P \
            -X stream \
            -R
    fi

    cp /etc/postgresql/postgresql.conf "$PGDATA/postgresql.conf"
    cp /etc/postgresql/pg_hba.conf "$PGDATA/pg_hba.conf"
    chown -R postgres:postgres "$PGDATA"
    rm -f "$REQUEST_FILE"
fi

exec docker-entrypoint.sh "$@"
