#!/usr/bin/env bash

ACTIVATOR_VERSION=1.3.2
ZIPFILE="typesafe-activator-${ACTIVATOR_VERSION}-minimal.zip"

wget http://downloads.typesafe.com/typesafe-activator/${ACTIVATOR_VERSION}/${ZIPFILE}

unzip -q ${ZIPFILE}
