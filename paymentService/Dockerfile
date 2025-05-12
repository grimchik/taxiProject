FROM openjdk:21-jdk-slim

WORKDIR /app

COPY paymentService/target/paymentService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8091

CMD ["java", "-jar", "app.jar"]
