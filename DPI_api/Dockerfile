FROM openjdk:8-jdk-alpine
ARG ENV_NAME
ENV SMART_API_HOME=/usr/smart-api/
ENV ENV_NAME=$ENV_NAME
WORKDIR $SMART_API_HOME
COPY target/smartdpi.jar smartdpi-api.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=${ENV_NAME}", "-jar", "smartdpi-api.jar"]