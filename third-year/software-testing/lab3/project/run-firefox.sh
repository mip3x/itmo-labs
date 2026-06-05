#!/usr/bin/env sh

set -u

./gradlew firefoxTest --rerun-tasks "$@"
status=$?

./print-test-summary.sh firefoxTest

exit "$status"
