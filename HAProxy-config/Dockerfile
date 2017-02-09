FROM alpine
LABEL MAINTAINER Vidya Sagar Kalvakunta sagarkrkv.github.io
LABEL VERSION 0.1

ENV CONSUL_TEMPLATE_VERSION=0.18.1

# Update wget to get support for SSL
RUN apk --update add haproxy

RUN apk --no-cache add ca-certificates wget openssl\
    && update-ca-certificates

# Download consul-template
RUN cd /tmp && \
    wget https://releases.hashicorp.com/consul-template/${CONSUL_TEMPLATE_VERSION}/consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.tgz && \
    tar -xvzf consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.tgz && \
    chmod a+x consul-template && \
    mv consul-template /usr/bin/consul-template &&\
    rm -rf /tmp/*

COPY haproxy.hcl /tmp/haproxy.hcl
COPY haproxy.ctmpl /tmp/haproxy.ctmpl

CMD ["consul-template", "-config=/tmp/haproxy.hcl"]
