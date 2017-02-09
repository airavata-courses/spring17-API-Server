#!/usr/bin/env bash

echo 'send HUP signal to reload consul-template config'
sudo ps -efww | grep consul-template | awk '{print $2}' | xargs kill -HUP
