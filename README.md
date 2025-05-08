### Quiz Platform

A microservices-based quiz platform with user management, quiz functionality, and notifications.

**Project Structure**

**user-service**: Handles user registration, authentication, and password reset.

**quiz-service**: Manages quiz creation and retrieval (TBD).

**notification-service**: Sends notifications via Kafka (TBD).

### Branches

**main**: Production-ready code.

**user-service**: Development for user-service.

**quiz-service**: Development for quiz-service.

**notification-service:** Development for notification-service.

### Setup

**Clone the repository:**

git clone https://github.com/Nariman-Haidar/QuizPlatformLast.git

Copy user-service/src/main/resources/application.yml.template to application.yml and configure environment variables.

**Run dependencies:**

docker-compose -f docker-compose.yml up -d

**Build and run:**

mvn clean package
mvn -f user-service/pom.xml spring-boot:run

### CI/CD

GitHub Actions runs on pushes to main, user-service, quiz-service, notification-service.

Currently builds and tests user-service. AWS deployment will be added later.

