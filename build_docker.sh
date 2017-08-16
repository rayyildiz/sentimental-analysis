#!/usr/bin/env bash

export GOOGLE_APPLICATION_CREDENTIALS="./src/main/resources/flux-decd0ff29d60.json"

sbt clean

sbt assembly

docker build -t rayyildiz/sentiment_analyzer .
