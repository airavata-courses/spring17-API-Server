FROM martinseeler/oracle-server-jre
RUN apk add --upgrade tar
RUN apk --update add wget ca-certificates
ADD api-server /opt/api-server
RUN ls
RUN wget -O curator-rpc.jar http://central.maven.org/maven2/org/apache/curator/curator-x-rpc/2.11.1/curator-x-rpc-2.11.1.jar && mv curator-rpc.jar /opt/api-server
EXPOSE 9190
RUN chmod +x /opt/api-server/start-api-server.sh
ENTRYPOINT ["/opt/api-server/start-api-server.sh"]
CMD ["start"]