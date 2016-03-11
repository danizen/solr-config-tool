#!/bin/bash
set -e

. ./ci/common

unzip -u $SOLR_FILE
$SOLR_DIR/bin/solr start -cloud -s ci/node/solr -p 8983  -m 512m
