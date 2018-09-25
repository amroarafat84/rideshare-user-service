FROM openjdk:8-jdk-alpine
ARG URL
ENV JDBC_URL=$URL
ARG USER
ENV JDBC_USERNAME=$USER
ARG PASS
ENG JDBC_PASSWORD=$PASS
COPY target/rideforce-user-service.jar /opt/lib/
ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/lib/rideforce-user-service.jar"]
