Mykare Application
=================
A Spring Boot application for user registration, authentication, and admin management.

## Features
- User registration with unique email
- Fetch public IP and country on registration
- Login validation API
- Basic authentication
- Admin APIs to list and delete users
- JUnit + Mockito tests
- Cucumber API tests
- Swagger documentation
- Dockerized application

## Tech Stack
- Java 17
- Spring Boot 3
- H2 Database
- Spring Security
- Swagger (springdoc-openapi)
- JUnit & Mockito
- Cucumber
- Docker

## Run Locally
```bash
mvn clean package
## Or
mvn clean install -DskipTests

java -jar target/mykare-0.0.1-SNAPSHOT.jar