FROM openjdk:21-jdk-slim

WORKDIR /app

COPY driverService/target/driverService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]
