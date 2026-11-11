#!/bin/sh
set -eu
exec VBoxManage startvm "WS_MMA_win" --type gui
