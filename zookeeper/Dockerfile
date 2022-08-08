FROM zookeeper:3.4

RUN /bin/bash -c  "cd bin; zkCli.sh create /election 's'"

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["zkServer.sh", "start-foreground"]

