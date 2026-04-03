# Leave Microservice

Leave management microservice for the Payroll system.

## Description

This Spring Boot microservice handles leave management functionality including:
- Leave request creation and tracking
- Leave balance management
- Integration with employee management system
- REST API endpoints for leave operations

## Technology Stack

- Java 17
- Spring Boot 3.3.1
- Spring Cloud Config
- Spring Data JPA
- H2 Database
- Netflix Eureka Client

## Docker Image

```bash
docker pull shaileshrathod647/leave:1.0.0
```

## Running the Container

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=default \
  -e SPRING_CONFIG_IMPORT=configserver:http://configserver:8071 \
  shaileshrathod647/leave:1.0.0
```

## Environment Variables

- `SPRING_PROFILES_ACTIVE`: Active Spring profile (default: default)
- `SPRING_APPLICATION_NAME`: Application name (default: leave)
- `SPRING_CONFIG_IMPORT`: Config server URL

## Health Check

Access actuator endpoints at:
- Health: http://localhost:8080/actuator/health
- Info: http://localhost:8080/actuator/info

## Part of Payroll System

This microservice is part of a larger payroll management system including:
- Config Server
- Eureka Server
- Gateway Server
- Employee Service
- Payroll Service
