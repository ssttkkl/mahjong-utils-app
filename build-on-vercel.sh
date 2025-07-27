#!/usr/bin/env sh

curl -L -o /tmp/openjdk-17.tar.gz https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.14%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.14_7.tar.gz
tar -xzf /tmp/openjdk-17.tar.gz -C /tmp/

export JAVA_HOME=/tmp/jdk-17.0.14+7
export PATH=$PATH:$JAVA_HOME/bin

./gradlew :kotlinUpgradeYarnLock composeApp:wasmJsBrowserDistribution --no-daemon
