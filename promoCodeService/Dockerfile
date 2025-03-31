FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl promoCodeService -am -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/promoCodeService/target/promoCodeService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8090

CMD ["java", "-jar", "app.jar"]
