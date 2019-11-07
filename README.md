Sentimental Analysis with Akka
===

Implementation of sentimental analysis by using [Google Natural Language API](https://cloud.google.com/natural-language/)  and [Akka Http](http://akka.io/)

[![Build Status](http://img.shields.io/travis/rayyildiz/sentimental-analysis.svg?style=flat-square)](https://travis-ci.org/rayyildiz/sentimental-analysis)
[![Build status](https://ci.appveyor.com/api/projects/status/aqlnj4skqgf10lfx?svg=true)](https://ci.appveyor.com/project/rayyildiz/sentimental-analysis)
[![Scala Version](https://img.shields.io/badge/scala-2.12.8-red.svg)](https://github.com/rayyildiz/sentimental-analysis/blob/master/build.sbt)
[![Akka Version](https://img.shields.io/badge/akka-2.5.23-blue.svg)](https://github.com/rayyildiz/sentimental-analysis/blob/master/project/Dependencies.scala)
[![Akka-Http Version](https://img.shields.io/badge/akka--http-10.1.8-orange.svg)](https://github.com/rayyildiz/sentimental-analysis/blob/master/project/Dependencies.scala)
[![GitHub license](https://img.shields.io/github/license/rayyildiz/sentimental-analysis.svg)](https://github.com/rayyildiz/sentimental-analysis/blob/master/LICENSE)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Frayyildiz%2Fsentimental-analysis.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Frayyildiz%2Fsentimental-analysis?ref=badge_shield)


This project was created for showing usage of:

* [Akka Actor model](http://doc.akka.io/docs/akka/current/scala/actors.html)
* [Akka Http](http://doc.akka.io/docs/akka-http/current/scala/http/)
* Dependency Injection in Akka System
* [Google Natural Language](https://cloud.google.com/natural-language/) And [Translation API](https://cloud.google.com/translate/docs/)

Install And Run
===

- First of all, create [Google Cloud](https://cloud.google.com/) account and create a project.
- You have to enable [Google Natural API](https://cloud.google.com/natural-language/). More information [click here.](https://cloud.google.com/natural-language/docs/getting-started)
- You have to enable [Google Transalation API](https://cloud.google.com/translate). More information [click here](https://cloud.google.com/translate/docs/getting-started)

Install
---
You need install ```sbt``` before . More info and install sbt click [Scala SBT](http://www.scala-sbt.org/0.13/docs/Setup.html)
Recommend version is **1.0.4**

Also you need to install Java 8. More info click [Jdk 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Google Natural API is free for first 5000 request per month.

Run
---
You need to define an environment variable ```GOOGLE_APPLICATION_CREDENTIALS``` which points a [Google Credential json file](https://cloud.google.com/docs/authentication/getting-started). You have enable API and define environment variable to point this file.


> $ ./run.sh
> test
>
> run
> [info] Running com.rayyildiz.sentiment_analyzer.BootApplication
> [INFO] Server online at http://localhost:8080/

Usage
===

Ping Pong Demo
---
Test akka actor model.

**POST** _http://localhost:8080/ping_ Ping-Pong demo. To test application.

> curl -H "Content-Type: application/json"  -X POST -d '{"message":"hello"}' http://localhost:8080/ping

Health monitor
---

Monitoring.

**GET** _http://localhost:8080/health_  Health monitor

> curl -H "Content-Type: application/json"  -X GET -d '{"message":"hello"}' http://localhost:8080/health

Clean Text
---

Clean Text (from url, html-xml tags, symbols etc)

**POST** _http://localhost:8080/api/clean_ Clean Text

> curl -H "Content-Type: application/json"  -X POST -d '{"text":"<b>This <i>is</i> a html message</b>"}' http://localhost:8080/api/clean

Detection language
---

Language Check (detect and check that language is English)

**POST** _http://localhost:8080/api/detect_ Detection language

> curl -H "Content-Type: application/json"  -X POST -d '{"text":"This is a message"}' http://localhost:8080/api/detect

Word extraction
---

Extract Important Words (topics mentioned or words that are not common, use a public api) Extract Words Indicating Sentiment (words that tend to show feeling)

**POST** _http://localhost:8080/api/extract_ Word extraction

> curl -H "Content-Type: application/json"  -X POST -d '{"text":"This is a message"}' http://localhost:8080/api/extract

Sentimental analysis
---

Sentimental detection.

**POST** _http://localhost:8080/api/sentiment_ Sentimental analysis

> curl -H "Content-Type: application/json"  -X POST -d '{"text":"I am happy"}' http://localhost:8080/api/sentiment

Relationship Determination
---

Relationship determination between the feature and sentiment.

**POST** _http://localhost:8080/api/determination_ Relationship determination between the feature and sentiment.

> curl -H "Content-Type: application/json"  -X POST -d '{"text":"Ask not what your country can do for you, ask what you can do for your country."}' http://localhost:8080/api/determination

All Analysis
---

Clean, language detection, extraction and sentimental analysis.

**POST** _http://localhost:8080/api/analysis_ Clean, detection, extract, sentiment, determination all together.

> curl -H "Content-Type: application/json"  -X POST -d '{"text":"Ask not what your country can do for you, ask what you can do for your country."}' http://localhost:8080/api/analysis


Alternative
===

You can use docker to run application. Run ```./build_docker.sh``` to build docker image, and ```./run_docker.sh``` to run container.
More info click [https://docs.docker.com/engine/installation/](https://docs.docker.com/engine/installation/)

Note: Building docker waits for a file like ```flux-decd0ff29d60.json```. For details look at [Google Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials) page.

Known Issues:
===

* Used [jsoup](https://jsoup.org/) to clean text. JSoup has a bug and performance issue for large text.


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Frayyildiz%2Fsentimental-analysis.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Frayyildiz%2Fsentimental-analysis?ref=badge_large)