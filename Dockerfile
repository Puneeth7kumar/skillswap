# Use official Java 21 image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy the rest of the project
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Expose the port
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "target/skillswaphub-0.0.1-SNAPSHOT.jar"]
