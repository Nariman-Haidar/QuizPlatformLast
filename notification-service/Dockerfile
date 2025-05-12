FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy service pom.xml and src
COPY notification-service/pom.xml .
COPY notification-service/src ./src
# Build from the directory with the pom.xml
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/notification-service-1.0-SNAPSHOT.jar notification-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "notification-service.jar"]