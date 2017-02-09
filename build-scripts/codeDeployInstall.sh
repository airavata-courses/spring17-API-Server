#!/usr/bin/env bash

#Running the server
# cd /home/ec2-user/api-deployment/spring17-API-Server/
# mvn clean install
# nohup java -jar mock-airavata_api_server/target/mock-airavata_api_server-0.15-SNAPSHOT.jar &

#Run airavata_api_server in docker container
sudo docker run --restart unless-stopped --net=host -d sagarkrkv/airavata_api_server

# Registering the service to consul using a python script
# cd /home/ec2-user/api-deployment/spring17-API-Server/build-scripts
# sudo pip install python-consul
# python register_service_consul.py
