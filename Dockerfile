FROM rayyildiz/java8:jre

WORKDIR /apps

ADD target/scala-2.12/sentimental-analysis-assembly-1.1.0-SNAPSHOT.jar /apps/analysis.jar

EXPOSE 8080

ENV GOOGLE_APPLICATION_CREDENTIALS=/apps/google_api.json

CMD ["/usr/bin/java","-jar","/apps/analysis.jar"]