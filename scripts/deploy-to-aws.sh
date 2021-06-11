#!/bin/sh

mvn package -DskipTests
rsync -avze "ssh -i ${KEY}" target/scrapebatch-0.0.1-SNAPSHOT.jar ${SERVER_USER}@${SERVER_ADDRESS}:${SERVER_PATH}
