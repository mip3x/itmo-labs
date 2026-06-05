#!/usr/bin/env sh

set -u

task_name="$1"
results_dir="build/test-results/$task_name"

if ! ls "$results_dir"/TEST-*.xml >/dev/null 2>&1; then
  echo
  echo "No test results found for $task_name"
  exit 0
fi

awk '
function attr(line, name,    pattern, value) {
  pattern = name "=\"[0-9]+\""
  if (match(line, pattern)) {
    value = substr(line, RSTART + length(name) + 2, RLENGTH - length(name) - 3)
    return value + 0
  }
  return 0
}

/^<testsuite / && $0 !~ /Gradle Test Run/ {
  tests += attr($0, "tests")
  skipped += attr($0, "skipped")
  failures += attr($0, "failures")
  errors += attr($0, "errors")
}

END {
  passed = tests - skipped - failures - errors
  print ""
  print "Summary for " task ":"
  print "  passed:  " passed
  print "  failed:  " failures
  print "  errors:  " errors
  print "  skipped: " skipped
  print "  total:   " tests
}
' task="$task_name" "$results_dir"/TEST-*.xml
