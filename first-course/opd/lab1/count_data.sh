#!/bin/bash

cd lab0

# 1)
# find . -type f -name "d*" -exec wc -l {} \; 2>>/tmp/errors >>/tmp/lines_count
wc -l d* */d* */*/d* 2>>/tmp/errors >/tmp/lines_count

# 2)
ls -lR 2>>/tmp/errors | grep -v '^$' | grep -v './' | grep -v 'total' | grep 'o$' | sort

# 3)
cat 2> /dev/null kabutops0 | grep -v 'e$'

# 4)
ls 2> /dev/null -Rl | grep -v '^$' | grep -v './' | grep -v 'total' | grep 'ma' | sort -nk2

# 5)
ls 2> /dev/null -Rl | grep -v '^$' | grep -v './' | grep -v 'total' | grep -E '\bp\w+' | head -n 3 | sort -nk2

# 6)
ls 2> /dev/null -Rl | grep -v '^$' | grep -v './' | grep -v 'total' | grep -E '\bs\w+' | sort -nk2
