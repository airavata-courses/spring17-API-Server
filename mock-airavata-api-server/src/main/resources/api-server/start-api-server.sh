#!/bin/sh

nohup java -jar curator-rpc.jar config.json &
java -jar api-server.jar