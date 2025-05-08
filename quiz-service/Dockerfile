FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY user-service user-service
COPY quiz-service quiz-service
COPY notification-service notification-service
RUN mvn -pl quiz-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/quiz-service/target/quiz-service-1.0-SNAPSHOT.jar quiz-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "quiz-service.jar"]