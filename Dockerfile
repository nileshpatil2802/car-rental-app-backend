# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first to leverage dependency caching
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Grant execution permissions to the Maven wrapper script
RUN chmod +x mvnw

# Download dependencies (cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy the actual source code
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# Stage 2: Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]