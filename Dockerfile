# Stage 1: Build the application
FROM gradle:8.7-jdk17 AS build
COPY --chown=gradle:gradle . /hstore/jobhunter
WORKDIR /hstore/jobhunter

#skip task: test
RUN gradle clean build -x test --no-daemon

# Stage 2: Run the application
FROM openjdk:17-slim
EXPOSE 8080

# Create directory for uploads and set permissions
RUN mkdir -p /app/upload && \
    chmod 777 /app/upload

# Copy the jar file
COPY --from=build /hstore/jobhunter/build/libs/*.jar /app/app.jar

# Set working directory
WORKDIR /app

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
