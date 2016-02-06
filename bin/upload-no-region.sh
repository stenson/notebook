#!/usr/bin/env bash

echo "~~~~~Indices~~~~~"
aws s3 sync --cache-control no-cache --exclude "*" --include "*.html" sites/$1/ s3://$1 --profile rob
echo "~~~~Cachables~~~~"
aws s3 sync sites/$1/ s3://$1 --profile rob
aws s3 sync sites/$1/ s3://$1 --profile rob