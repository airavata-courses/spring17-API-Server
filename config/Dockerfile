FROM alpine
LABEL MAINTAINER Vidya Sagar Kalvakunta sagarkrkv.github.io
LABEL VERSION 0.1

ENV CONSUL_TEMPLATE_VERSION=0.18.1

# Update wget to get support for SSL
RUN apk --update add haproxy

RUN apk --no-cache add ca-certificates wget openssl\
    && update-ca-certificates

# Download consul-template
RUN ( wget https://github.com/hashicorp/consul-template/releases/download/v${CONSUL_TEMPLATE_VERSION}/consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.tar.gz -O /tmp/consul_template.tar.gz && gunzip /tmp/consul_template.tar.gz && cd /tmp && tar xf /tmp/consul_template.tar && cd /tmp/consul-template* && mv consul-template /usr/bin && rm -rf /tmp/* )

COPY haproxy.hcl /tmp/haproxy.hcl
COPY haproxy.ctmpl /tmp/haproxy.ctmpl

CMD ["consul-template", "-config=/tmp/haproxy.hcl"]
