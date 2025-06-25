# Use Eclipse Temurin OpenJDK 21 as the base image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the built jar file
COPY target/day_scheduler-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Set environment variables for MongoDB (can be overridden at runtime)
ENV SPRING_DATA_MONGODB_HOST=mongodb
ENV SPRING_DATA_MONGODB_PORT=27017
ENV SPRING_DATA_MONGODB_DATABASE=day_scheduler

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 