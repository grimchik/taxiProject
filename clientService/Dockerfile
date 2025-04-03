FROM openjdk:21-jdk-slim

WORKDIR /app

COPY clientService/target/clientService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
