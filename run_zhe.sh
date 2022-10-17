#!/usr/bin/env bash

set -e

log_file="$1"

[ -z "$log_file" ] && echo "Usage: $0 <path-to-log-file>" && exit 1

zhe_jar='ParSy/build/libs/ParSy-0.1.jar'

if [ ! -f "$zhe_jar" ]; then
    echo "Unable to find Zhefuscator JAR" && exit 1
fi

logs_sep_by_space=$(head -40 "$log_file" | awk -e '{printf "\x27"$0"\x27 "}')

eval java -jar "$zhe_jar" "$logs_sep_by_space"

echo ""
