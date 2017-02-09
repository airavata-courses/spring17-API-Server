template {
  source = "/home/ec2-user/haproxy-config/haproxy.ctmpl"
  destination = "/tmp/haproxy.cfg"
  command = "sudo nl-qdisc-add --dev=lo --parent=1:4 --id=40: --update plug --buffer &> /dev/null && haproxy -f /tmp/haproxy.cfg -p /tmp/haproxy.pid -sf $(cat /tmp/haproxy.pid) &> /home/ec2-user/haproxy.log  && sudo nl-qdisc-add --dev=lo --parent=1:4 --id=40: --update plug--release-indefinite &> /dev/null"
}
