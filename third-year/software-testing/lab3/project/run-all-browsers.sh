#!/usr/bin/env sh

set -u

./run-chrome.sh "$@" &
chrome_pid=$!

./run-firefox.sh "$@" &
firefox_pid=$!

chrome_status=0
firefox_status=0

wait "$chrome_pid" || chrome_status=$?
wait "$firefox_pid" || firefox_status=$?

if [ "$chrome_status" -ne 0 ] || [ "$firefox_status" -ne 0 ]; then
  exit 1
fi
