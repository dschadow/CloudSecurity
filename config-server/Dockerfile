FROM openjdk:8-jre-alpine
MAINTAINER Dominik Schadow <dominikschadow@gmail.com>

VOLUME /tmp

EXPOSE 8888

ADD target/config-server.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]