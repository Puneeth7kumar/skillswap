# Use official Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (to leverage Docker cache)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy the rest of the project
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the Spring Boot application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Expose the port your app will run on
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "target/skillswapbackend-0.0.1-SNAPSHOT.jar"]
