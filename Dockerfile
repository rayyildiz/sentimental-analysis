FROM rayyildiz/java8:jre

RUN mkdir -p /app/conf

ADD target/scala-2.12/sentimental-analysis-assembly-1.0.jar /app/analysis.jar

ADD flux-decd0ff29d60.json  /app/conf/flux-decd0ff29d60.json

RUN export GOOGLE_APPLICATION_CREDENTIALS="/app/conf/flux-decd0ff29d60.json"

EXPOSE 8080

CMD ["/usr/bin/java","-jar","/app/analysis.jar"]