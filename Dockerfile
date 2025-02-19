ARG BUILD_PLATFORM
FROM --platform=${BUILD_PLATFORM} amazoncorretto:17-alpine3.21-jdk
WORKDIR /opt/app
COPY target/springboot-build-pipeline-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]