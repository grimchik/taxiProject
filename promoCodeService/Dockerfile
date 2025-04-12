FROM openjdk:21-jdk-slim

WORKDIR /app

COPY promoCodeService/target/promoCodeService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8090

CMD ["java", "-jar", "app.jar"]
