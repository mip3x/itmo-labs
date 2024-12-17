#!/bin/bash

./gradlew clean build
cp ./build/libs/lab3.war ../wildfly/standalone/deployments/ROOT.war
