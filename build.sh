#!/bin/bash
#set -ex

mkdir -p output
#mvn clean package
mvn clean package -D skipTests
cp target/flink-sink-1.9-byted-SNAPSHOT.jar output/