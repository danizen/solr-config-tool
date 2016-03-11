#!/bin/bash
set -e

. ./ci/common

curl -o $SOLR_FILE $SOLR_URL
SOLR_ACTUAL_SHA=`sha1sum $SOLR_FILE | cut -d' ' -f1`

if [ "$SOLR_EXPECT_SHA" != "$SOLR_ACTUAL_SHA" ]; then
  echo "Failed to download Solr $SOLR_VERSION" 1>&2
  exit 1
else
  echo "Successfully downloaded Solr $SOLR_VERSION"
fi

