FROM openjdk:17-alpine

VOLUME /tmp

ARG JAR_FILE=build/libs/Auth_Server_v1-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} member_read.jar

EXPOSE 8083

ENTRYPOINT ["java","-jar","/auth_server_v1.jar"]