FROM openjdk:17-jdk-alpine
WORKDIR /opt/app
COPY target/springboot-build-pipeline-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]