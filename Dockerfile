FROM openjdk:11-jre-slim-buster

ENV DEBIAN_FRONTEND=noninteractive
RUN apt update && apt install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY ./app/build/libs/geo-filtering.jar /app
CMD "java" "-jar" "geo-filtering.jar"