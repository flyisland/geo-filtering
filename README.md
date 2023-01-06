# Geo Filtering

This is a simple implementation of Aaron's essay [Ranged Filtering of Streaming Numeric Data, or Geolocation Filtering of Streaming GPS Data, using Topic-Based Pub/Sub Messaging](http://worldcomp-proceedings.com/proc/p2016/ICM3967.pdf).

It also works as a [OBO subscription managers](https://docs.solace.com/API/API-Developer-Guide/Managing-Subscriptions.htm#Manage-On-Behalf) to subscribe to a large number of topics for the Web client.

## Build Docker Image

1. run `./gradlew jar` first to build the flat jar file
2. run `docker build . -t ichen/geo-filtering:0.0.1` to build the Docker image

## Build and Run Docker Container

1. run below command to create the container
1. run `docker start ichen-geo-filtering` to start the data generator

```bash
docker create --restart unless-stopped \
--env 'solace_host=host_url' \
--env 'solace_username=username@vpn' \
--env 'solace_password=password' \
--name=ichen-geo-filtering ichen/geo-filtering:0.0.1
```
