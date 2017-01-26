#!/usr/bin/env bash
command_exists() {
        command -v "$@" > /dev/null 2>&1
}
if command_exists docker; then
                version="$(docker -v | awk -F '[ ,]+' '{ print $3 }')"
                MAJOR_W=1
                MINOR_W=10
		echo 'Docker is already installed'

else
		echo 'Installing Docker'
                sudo yum update -y
                sudo yum install -y docker
                sudo service docker start
fi


echo 'Removing the previous image'
if  [ "$(sudo docker images | grep "^<none>" | awk '{print $3}')" != "" ]; then
	sudo docker rmi $(sudo docker images | grep "^<none>" | awk '{print $3}')
fi

echo 'Removing the previous images with exit status'
if  [ "$(sudo docker ps -a | grep Exit  )" != "" ]; then
	sudo docker ps -a | grep Exit | cut -d ' ' -f 1 | xargs sudo docker rm
fi

echo 'check if consul is running'
if [[ "$(sudo docker ps -q --filter ancestor=consul)" == "" ]]; then
    #Allow alias to be used in non-interactive shell
    shopt -s expand_aliases

    # Creating alias for determining the wan ip address and lan ip address of the instance
    alias wanip="dig +short myip.opendns.com @resolver1.opendns.com"
    alias lanip="ifconfig eth0 | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'"

    
    #run consul on instance
    sudo docker run --net=host  -d  -e 'CONSUL_LOCAL_CONFIG={"translate_wan_addrs": true}' consul \
            agent  -advertise $(lanip) -ui -advertise-wan $(wanip) -client=0.0.0.0 -retry-join-wan=52.14.96.95
fi


cd /home/ec2-user/api-deployment/
rm -rf spring17-API-Server
mkdir spring17-API-Server
