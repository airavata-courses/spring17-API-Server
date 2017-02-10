#!/usr/bin/env bash


sudo mv -f  /home/ec2-user/codeDeploy/haproxy-config/ /home/ec2-user/haproxy-config

echo 'send HUP signal to reload consul-template config'
sudo ps -efww | grep consul-template | awk '{print $2}' | xargs kill -HUP
