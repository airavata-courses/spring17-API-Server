template {
  source = "/home/ec2-user/haproxy-config/haproxy.ctmpl"
  destination = "/tmp/haproxy.cfg"
  command = "haproxy -f /tmp/haproxy.cfg -p /tmp/haproxy.pid -sf $(cat /tmp/haproxy.pid) &> /home/ec2-user/haproxy.log "
}
