#!/bin/bash
set -e
mvn install
nf-test test tests/**/*.nf.test --plugins target/nft-csv-*.jar