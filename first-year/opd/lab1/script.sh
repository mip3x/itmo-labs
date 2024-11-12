#!/bin/bash

sh create_files.sh
sh write_data.sh
sh set_permissions.sh
sh create_links.sh

sh set_permissions.sh

sh count_data.sh
sh remove_files.sh

sh set_permissions.sh

# tree
