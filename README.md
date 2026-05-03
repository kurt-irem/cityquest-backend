# CityQuest Backend

Spring Boot REST API for the CityQuest application. Manages users, places, visits, and collections with PostgreSQL database.

---

Frontend repository: https://github.com/kurt-irem/cityquest-frontend 

## Features

- Secure user authentication and management  
  (JWT access & refresh tokens, refresh tokens stored in HttpOnly Secure cookies, HTTPS required)
- CRUD operations for places, visits, and collections  
- Achievements and goals system  
- Database migrations using Flyway  

## Setup

```sh
mvn clean install
mvn spring-boot:run
```

## Available Commands

- `mvn clean install` - Install dependencies
- `mvn spring-boot:run` - Start application
- `mvn test` - Run tests
- `mvn clean package` - Build JAR

## API Documentation

Base URL: `http://localhost:8080/api`

**Main Endpoints:**
- `GET /places` - Get all places
- `POST /places` - Create new place
- `GET /users/{id}/visits` - Get user visits
- `POST /collections` - Create collection
- `GET /collections/{id}` - Get collection


## Stack

- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Flyway for database migrations
- Maven for build management
