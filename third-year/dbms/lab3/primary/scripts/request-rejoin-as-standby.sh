#!/bin/bash

set -euo pipefail

SOURCE_HOST="${1:?usage: request-rejoin-as-standby.sh SOURCE_HOST [SOURCE_PORT]}"
SOURCE_PORT="${2:-5432}"
REQUEST_FILE="/tmp/rejoin-as-standby.conf"
PGDATA="${PGDATA:-/var/lib/postgresql/data}"

cat > "$REQUEST_FILE" <<EOF
SOURCE_HOST=$SOURCE_HOST
SOURCE_PORT=$SOURCE_PORT
EOF

echo "Rejoin request saved: source=$SOURCE_HOST:$SOURCE_PORT"
echo "Stopping PostgreSQL; container restart will perform pg_basebackup before startup."

pg_ctl -D "$PGDATA" -m fast stop
