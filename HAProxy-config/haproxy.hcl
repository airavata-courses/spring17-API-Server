template {
  source = "/tmp/haproxy.ctmpl"
  destination = "/etc/haproxy/haproxy.cfg"
  command = "sudo nl-qdisc-add --dev=lo --parent=1:4 --id=40: --update plug --buffer &> /dev/null && haproxy -f /tmp/haproxy.cfg -p /tmp/haproxy.pid -sf $(cat /tmp/haproxy.pid) && sudo nl-qdisc-add --dev=lo --parent=1:4 --id=40: --update plug--release-indefinite &> /dev/null"
}
