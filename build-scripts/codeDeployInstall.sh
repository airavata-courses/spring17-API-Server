#!/usr/bin/env bash

#Running the server
cd /home/ec2-user/api-deployment/spring17-API-Server/
mvn clean install
nohup java -jar mock-airavata-api-server/target/mock-airavata-api-server-0.15-SNAPSHOT.jar &

# Registering the service to consul using a python script
# cd /home/ec2-user/api-deployment/spring17-API-Server/build-scripts
# sudo pip install python-consul
# python register_service_consul.py
