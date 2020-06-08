#!/bin/bash
set -ex

mkdir -p output
mvn clean package
cp target/flink-byted-quickstart-java-1.9-byted-SNAPSHOT.jar output
cp script/* output
