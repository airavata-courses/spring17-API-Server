log_level = "debug"

// This is the path to store a PID file which will contain the process ID of the
// Consul Template process. This is useful if you plan to send custom signals
// to the process.
pid_file = "/tmp/ctmpl.pid"

template {
  source = "/home/ec2-user/haproxy-config/haproxy.ctmpl"
  destination = "/tmp/haproxy.cfg"
  command = "haproxy -f /tmp/haproxy.cfg -p /tmp/haproxy.pid -sf $(cat /tmp/haproxy.pid)"
}
