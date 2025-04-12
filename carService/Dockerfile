FROM openjdk:21-jdk-slim

WORKDIR /app

COPY carService/target/carService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8083

CMD ["java", "-jar", "app.jar"]
