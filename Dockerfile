# Use the official Java 17 image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/spring-proxy-1.0.jar app.jar

# Copy the JAR file into the container
ENTRYPOINT ["java", "-jar", "app.jar"]