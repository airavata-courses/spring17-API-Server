#!/usr/bin/env bash

# Update the operating System
sudo yum -y update

# Install aws-cli if not installed already
sudo yum install -y aws-cli

# Move to user home directory
cd /home/ec2-user

# aws configuration
su - ec2-user -c 'echo "$AWS_ACCESS_KEY
$AWS_SECRET_ACCESS_KEY


" | aws configure'

# setting up s3 access for aws code deploy
aws s3 cp s3://aws-codedeploy-us-east-2/latest/install . --region us-east-2
chmod +x ./install
sed -i "s/sleep(.*)/sleep(10)/" install
sudo ./install auto

# start aws code deploy agent if not started already
sudo service codedeploy-agent start

# Pushing developers public keys to .ssh/authorized_keys
su - ec2-user -c 'echo $ANUJ_SSH_KEY >> /home/ec2-user/.ssh/authorized_keys'
su - ec2-user -c 'echo $SAGAR_SSH_KEY >> /home/ec2-user/.ssh/authorized_keys'
su - ec2-user -c 'echo $SUPREET_SSH_KEY >> /home/ec2-user/.ssh/authorized_keys'

#install docker engine and start it
sudo yum install -y docker
sudo service docker start

#Allow alias to be used in non-interactive shell
shopt -s expand_aliases

# Creating alias for determining the wan ip address and lan ip address of the instance
alias wanip="dig +short myip.opendns.com @resolver1.opendns.com"
alias lanip="ifconfig eth0 | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'"

#adding the aliases to .bashrc to make them persistant
echo "alias wanip=\"dig +short myip.opendns.com @resolver1.opendns.com\"" >> .bashrc
echo "alias lanip=\"ifconfig eth0 | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'\"" >> .bashrc

#run consul on instance
sudo docker run --net=host  -d  -e 'CONSUL_LOCAL_CONFIG={"translate_wan_addrs": true}' consul \
        agent  -advertise $(lanip) -ui -advertise-wan $(wanip) -client=0.0.0.0 -retry-join=52.14.96.95

 #-retry-join-ec2-tag-key name -retry-join-ec2-tag-value spring17-API-loadBalancer-instance

# deploying spring17-api-gateway
sudo docker run --restart unless-stopped --net=host -d sagarkrkv/airavata_api_server

# Test deployment, uncomment below line
# java -jar mock-airavata-api-client/target/mock-airavata-api-client-0.15-SNAPSHOT.jar
