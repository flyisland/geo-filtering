FROM openjdk:11-jre-slim-buster

WORKDIR /app
COPY ./app/build/libs/geo-filtering.jar /app
CMD "java" "-jar" "geo-filtering.jar"