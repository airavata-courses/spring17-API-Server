#!/usr/bin/env bash

# Update the operating System
sudo yum -y update

#installing open-jdk
sudo yum install -y java-1.8.0-openjdk-devel.x86_64

#installing apache-maven for building
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

# Setting default JDK to 1.8
sudo update-alternatives --set java /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java
sudo update-alternatives --set javac /usr/lib/jvm/java-1.8.0-openjdk.x86_64/bin/javac

# Installing git version control
sudo yum install -y git-all.noarch

# Install aws-cli if not installed already
sudo yum install -y aws-cli

# Move to user home directory
cd /home/ec2-user

# aws configuration
echo "$AWS_ACCESS_KEY
$AWS_SECRET_ACCESS_KEY


" | aws configure

# setting up s3 access for aws code deploy
aws s3 cp s3://aws-codedeploy-us-east-2/latest/install . --region us-east-2
chmod +x ./install
sed -i "s/sleep(.*)/sleep(10)/" install
sudo ./install auto

# start aws code deploy agent if not started already
sudo service codedeploy-agent start

# deploying spring17-api-gateway
mkdir api-deployment
cd api-deployment/
git clone https://github.com/airavata-courses/spring17-API-Server.git
cd spring17-API-Server/
git checkout develop
mvn clean install
nohup java -jar mock-airavata-api-server/target/mock-airavata-api-server-0.15-SNAPSHOT.jar &

# Test deployment, uncomment below line
# java -jar mock-airavata-api-client/target/mock-airavata-api-client-0.15-SNAPSHOT.jar