#!/usr/bin/env bash

# Update the operating System
sudo yum -y update

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

#run consul in server mode on instance
sudo docker run --restart unless-stopped --net=host  -d  -e 'CONSUL_LOCAL_CONFIG={"translate_wan_addrs": true}' consul \
        agent -server -bootstrap-expect=1  -advertise $(lanip) -ui -advertise-wan $(wanip) -client=0.0.0.0


# running fabio
sudo docker run --restart unless-stopped --net=host  -d magiconair/fabio

# install HAProxy
sudo yum install -y haproxy

#install consul-template
cd /tmp
wget https://releases.hashicorp.com/consul-template/0.18.1/consul-template_0.18.1_linux_amd64.tgz
tar -xvzf consul-template_0.18.1_linux_amd64.tgz
sudo chmod a+x consul-template
sudo mv consul-template /usr/bin/consul-template
rm -rf /tmp/consul-template_0.18.1_linux_amd64.tgz

cd /home/ec2-user

#get loadbalancer config
wget https://s3.us-east-2.amazonaws.com/spring17-api-gateway-travis-ci/loadbalancer.zip
unzip loadbalancer.zip
rm -rf loadbalancer.zip build-scripts appspec.yml

nohup sudo consul-template -config=haproxy-config/haproxy.ctmpl &> ctmpl.log &
