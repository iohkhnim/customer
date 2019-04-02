FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar customer-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/customer-0.0.1-SNAPSHOT.jar"]