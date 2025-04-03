FROM openjdk:21-jdk-slim

WORKDIR /app

COPY rateService/target/rateService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8085

CMD ["java", "-jar", "app.jar"]
