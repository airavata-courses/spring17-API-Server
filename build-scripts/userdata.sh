#!/usr/bin/env bash

#installing open-jdk
sudo yum install -y java-1.8.0-openjdk-devel.x86_64

#installing apache-maven for building
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

# Setting default JDK to 1.8
echo 1 | sudo update-alternatives --config java
echo 1 | sudo update-alternatives --config javac

# Installing git version control
sudo yum install -y git-all.noarch

# deploying api
mkdir api-deployment
cd api-deployment/
git clone https://github.com/airavata-courses/spring17-API-Server.git
cd spring17-API-Server/
git checkout develop
mvn clean install
nohup java -jar mock-airavata-api-server/target/mock-airavata-api-server-0.15-SNAPSHOT.jar &

# Test deployment, uncomment below line
# java -jar mock-airavata-api-client/target/mock-airavata-api-client-0.15-SNAPSHOT.jar