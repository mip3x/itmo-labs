#!/usr/bin/env sh

set -u

./gradlew chromeTest --rerun-tasks "$@"
status=$?

./print-test-summary.sh chromeTest

exit "$status"
