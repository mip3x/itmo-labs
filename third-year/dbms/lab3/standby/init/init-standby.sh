#!/bin/bash

set -e

# Wait for primary to be ready
until pg_isready -h primary -p 5432 -U postgres; do
  echo "Waiting for primary to be ready..."
  sleep 2
done

# Stop the server
pg_ctl stop -D "$PGDATA"

# Clean up the data directory
rm -rf "$PGDATA"/*
echo "Data directory cleaned up"

# Perform base backup and write standby.signal + primary_conninfo
PGPASSWORD='replicator_password' pg_basebackup -h primary -D "$PGDATA" -U replicator -v -P --wal-method=stream -R
echo "Base backup completed"

# Set permissions
chown -R postgres:postgres "$PGDATA"

# Copy conf files
cp /etc/postgresql/postgresql.conf "$PGDATA/postgresql.conf"
cp /etc/postgresql/pg_hba.conf "$PGDATA/pg_hba.conf"
echo "Conf files copied"

# Start the server
pg_ctl -D "$PGDATA" start
