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
mvn clean package
## Or(Skipping Tests)
mvn clean install -DskipTests

java -jar target/mykare-0.0.1-SNAPSHOT.jar --debug

application runs on port 8080 by default.

## Create User
http://localhost:8080/api/users/register

{
"name": "Thomas Doe",
"email": "thomas.doe@example.com",
"gender": "MALE",
"password": "SecurePass124"
}



## Login User

http://localhost:8080/api/users/login
{
"email": "thomas.doe@example.com",
"password": "SecurePass124"
}


Accessing Swagger UI:
http://localhost:8080/swagger-ui/index.html




A comprehensive Spring Boot application that provides user registration, authentication, and management features with IP location tracking.

## Features

- ✅ User Registration with email uniqueness validation
- ✅ IP Address and Country detection using external APIs
- ✅ User Authentication and Validation
- ✅ Basic Authentication Security
- ✅ Admin-only endpoints for user management
- ✅ Comprehensive JUnit test cases
- ✅ Cucumber BDD test scenarios
- ✅ Swagger API documentation
- ✅ Docker containerization
- ✅ Exception handling and validation

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** for authentication
- **Spring Data JPA** for data persistence
- **H2 Database** for development and testing
- **JUnit 5** and **Mockito** for unit testing
- **Cucumber** for behavior-driven testing
- **Swagger/OpenAPI 3** for API documentation
- **Docker** for containerization

## Prerequisites

- Java 17 or later
- Maven 3.6+
- Docker (optional, for containerized deployment)

## Getting Started

### 1. Clone the Repository

bash
git clone <repository-url>
cd mykare


### 2. Build the Project

bash
mvn clean compile


### 3. Run Tests

bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest="**/*Test"

# Run only cucumber tests
mvn test -Dtest="CucumberTestRunner"


### 4. Run the Application

bash
mvn spring-boot:run


The application will start on `http://localhost:8080`

### 5. Access Swagger Documentation

Visit `http://localhost:8080/swagger-ui.html` to explore the API documentation.

### 6. Access H2 Database Console

Visit `http://localhost:8080/h2-console` with the following credentials:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## API Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register a new user |
| POST | `/api/users/login` | Validate user credentials |

### Admin-Only Endpoints (Basic Auth Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all registered users |
| DELETE | `/api/users/{email}` | Delete user by email |

### Default Admin Credentials

- **Email**: `admin@example.com`
- **Password**: `admin`

## API Usage Examples

### 1. Register a New User

bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "gender": "MALE",
    "password": "password123"
  }'


### 2. Login User

bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'


### 3. Get All Users (Admin Only)

bash
curl -X GET http://localhost:8080/api/users \
  -u admin@example.com:admin


### 4. Delete User (Admin Only)

bash
curl -X DELETE http://localhost:8080/api/users/john.doe@example.com \
  -u admin@example.com:admin


## Docker Deployment

### 1. Build the Application

bash
mvn clean package -DskipTests


### 2. Build Docker Image

bash
docker build -t user-management-api .


### 3. Run with Docker Compose

bash
docker-compose up -d


### 4. Run Single Container

bash
docker run -p 8080:8080 user-management-api


## Running with Different Profiles

### Development Profile

bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev


### Docker Profile

bash
mvn spring-boot:run -Dspring-boot.run.profiles=docker


## External API Integration

The application integrates with the following external APIs:

1. **IP Address Detection**: `https://api.ipify.org`
2. **Location Detection**: `http://ip-api.com/json/{ip}`

These APIs are used to automatically capture and store user location information during registration.

## Testing

### Unit Tests

The application includes comprehensive unit tests for:
- **UserService**: Registration, validation, user management
- **IpLocationService**: IP and location detection
- **Controllers**: API endpoint testing

### Integration Tests

Cucumber tests cover:
- User registration scenarios
- Authentication workflows
- Admin functionality
- Error handling

### Test Coverage

Run tests with coverage:

bash
mvn clean test jacoco:report


Coverage reports will be available at `target/site/jacoco/index.html`

## Security Configuration

- **Password Encoding**: BCrypt encryption
- **Basic Authentication**: For admin endpoints
- **Role-based Access Control**: USER and ADMIN roles
- **CSRF Protection**: Disabled for stateless API
- **Session Management**: Stateless configuration

## Database Schema

### Users Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| gender | VARCHAR(10) | NOT NULL |
| password | VARCHAR(255) | NOT NULL |
| ip_address | VARCHAR(45) | |
| country | VARCHAR(100) | |
| role | VARCHAR(10) | NOT NULL, DEFAULT 'USER' |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |