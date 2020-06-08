#!/bin/bash
set -ex

mkdir -p output
mvn clean package
cp target/project.jar output
cp script/* output
