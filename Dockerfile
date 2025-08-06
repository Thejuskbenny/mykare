FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/mykare-1.0.0.jar app.jar #update with your actual JAR file name

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]