#!/bin/sh

mvn package -Pnative
rsync -avze "ssh -i ${KEY}" target/scrapebatch-0.0.1-SNAPSHOT-runner ${SERVER_USER}@${SERVER_ADDRESS}:${SERVER_PATH}
