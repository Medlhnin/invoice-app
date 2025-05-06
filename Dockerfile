FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT exec java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}

