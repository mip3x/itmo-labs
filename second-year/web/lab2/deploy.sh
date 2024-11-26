#!/bin/bash

./gradlew clean build
cp ./build/libs/lab2.war ./wildfly/standalone/deployments/ROOT.war
