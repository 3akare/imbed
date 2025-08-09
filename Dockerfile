# Use OpenJDK 17
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy and package the application
COPY . /app
RUN mvn clean package -DskipTests

# Run the executable JAR
CMD ["java", "-jar", "target/imbed-1.0-SNAPSHOT.jar"]
