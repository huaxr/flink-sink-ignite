#!/bin/bash
#set -ex

mkdir -p output
#mvn clean package
mvn clean package -D skipTests -U -Pbuild-jar
cp target/flink-byted-quickstart-java-1.9-byted-SNAPSHOT.jar output/