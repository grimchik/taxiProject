FROM openjdk:21-jdk-slim

WORKDIR /app

COPY eurekaServer/target/eurekaServer-1.0-SNAPSHOT.jar app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]