#!/usr/bin/env bash

export GOOGLE_APPLICATION_CREDENTIALS="./flux-decd0ff29d60.json"

sbt clean

sbt assembly

docker build -t rayyildiz/sentiment_analyzer .
