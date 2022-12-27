FROM openjdk:11-jdk-slim-stretch
WORKDIR /app
COPY ./app/build/libs/geo-filtering.jar /app
CMD "java" "-jar" "geo-filtering.jar"