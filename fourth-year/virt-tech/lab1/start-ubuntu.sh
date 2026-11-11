#!/bin/sh
set -eu
# exec VBoxManage startvm "WS_MMA_ubuntu" --type gui
exec VBoxManage startvm "WS_MMA_ubuntu" --type headless
