#!/bin/bash

./gradlew clean build
cp ./build/libs/lab4.war ../../web/wildfly/standalone/deployments/ROOT.war
