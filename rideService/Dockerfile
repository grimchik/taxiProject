FROM openjdk:21-jdk-slim

WORKDIR /app

COPY rideService/target/rideService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8084

CMD ["java", "-jar", "app.jar"]
