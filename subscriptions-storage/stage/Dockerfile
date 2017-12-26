FROM openjdk:latest
LABEL MAINTAINER="dgridnev"
WORKDIR /opt/docker
ADD --chown=daemon:daemon opt /opt
USER daemon
ENTRYPOINT ["bin/subscriptions-storage"]
CMD []
