# Geo Filtering

This is a simple implementation of Aaron's essay [Ranged Filtering of Streaming Numeric Data, or Geolocation Filtering of Streaming GPS Data, using Topic-Based Pub/Sub Messaging](http://worldcomp-proceedings.com/proc/p2016/ICM3967.pdf).

It also works as a [OBO subscription managers](https://docs.solace.com/API/API-Developer-Guide/Managing-Subscriptions.htm#Manage-On-Behalf) to subscribe to a large number of topics for the Web client.

## Prerequisites

Enable subscription management capability of the client first. 

![](./obo.avif)

## Options

1. run `./gradlew jar` first to build the flat jar file

```shell
 java -jar app/build/libs/geo-filtering.jar --help
Usage: geo-filtering [-h] [-H=<host>] [-p=<password>] [-u=<userName>]
  -h, --help          display this help message
  -H, --host=<host>   ip[:port]  IP and port of the event broker. (e.g. -h=192.
                        168.160.101), if not specified, read from the env
                        variable solace_host
                        Default: localhost:44444
  -p, --password=<password>
                      Client password, if not specified, read from the env
                        variable solace_password
                        Default: default
  -u, --username=<userName>
                      user[@vpn] Client username and optionally VPN name, , if
                        not specified, read from the env variable
                        solace_username
                        Default: default@default
```

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
