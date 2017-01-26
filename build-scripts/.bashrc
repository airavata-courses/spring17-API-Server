alias wanip="dig +short myip.opendns.com @resolver1.opendns.com"
alias lanip="ifconfig eth0 | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'"
