#!/bin/sh
exec find "${1:-.}" \( \
    -type f -name '*.ipr' -or \
    -type f -name '*.iml' -or \
    -type f -name '*.iws' -or \
    -type d -name '.idea' -or \
    -path '*/.idea/*'         \
\) -delete