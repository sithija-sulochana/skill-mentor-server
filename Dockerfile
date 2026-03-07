FROM maven:3.9.6-eclipse-temurin-17
WORKDIR /app

# Copy the entire project
COPY . .

# Build the application
# Note: Ensure you have deleted the 'springdoc-openapi-ui' (v1.7.0) 
# dependency from your pom.xml before running this build!
RUN mvn clean package -DskipTests

# Standardize on port 8080
EXPOSE 8080

# Run the application and explicitly bind to port 8080
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "target/skillmentor-0.0.1-SNAPSHOT.jar"]