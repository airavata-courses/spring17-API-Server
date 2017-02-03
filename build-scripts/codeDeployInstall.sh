#!/usr/bin/env bash

cd /home/ec2-user/api-deployment/spring17-API-Server/
mvn clean install
nohup java -jar mock-airavata-api-server/target/mock-airavata-api-server-0.15-SNAPSHOT.jar &
pip install python-consul
python register_service_consul.py