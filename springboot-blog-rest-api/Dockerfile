# Use the official OpenJDK 11 base image
FROM openjdk:17-jdk-slim

# Arguments for the JAR file
ARG JAR_FILE=target/*.jar

# Copy the JAR file to the image
COPY ${JAR_FILE} app.jar

# Command to run the application
ENTRYPOINT ["java","-jar","/app.jar"]
