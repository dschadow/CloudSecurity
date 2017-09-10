FROM openjdk:8-jre-alpine
MAINTAINER Dominik Schadow <dominikschadow@gmail.com>

EXPOSE 8888

COPY target/config-server.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]