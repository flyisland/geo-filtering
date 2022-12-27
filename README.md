## Docker version

1. run `sudo docker build . -t ichen/geo-filtering:0.0.1` to build the Docker image
1. run below command to create the container
1. run `sudo docker start ichen-geo-filtering` to start the data generator

```bash
sudo docker create --restart unless-stopped \
--env 'solace_host=host_url' \
--env 'solace_username=username@vpn' \
--env 'solace_password=password' \
--name=ichen-geo-filtering ichen/geo-filtering:0.0.1
``` 
