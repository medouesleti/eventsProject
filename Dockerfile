# Use the official OpenJDK 8 image with Alpine
FROM openjdk:8-jdk-alpine

# Install bash
RUN apk add --no-cache bash

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/*.jar app.jar

# Copy the wait-for-it.sh script into the container
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Expose the port that Spring Boot will listen on
EXPOSE 8082

# Start the application with wait-for-it.sh
ENTRYPOINT ["/app/wait-for-it.sh", "mysql-db:3306", "--timeout=60", "--", "java", "-jar", "app.jar"]
