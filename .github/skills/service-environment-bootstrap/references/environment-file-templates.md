# Environment File Templates

Use these as starting points when creating files for a service/environment pair.

## 1. `application-<env>.properties`

```properties
# Keep only environment-specific overrides.
server.port=${SERVER_PORT:4000}

spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

spring.kafka.bootstrap-servers=${SPRING_KAFKA_BOOTSTRAP_SERVERS:kafka:9092}

billing.service.address=${BILLING_SERVICE_ADDRESS:billing-service}
billing.service.grpc.port=${BILLING_SERVICE_GRPC_PORT:9090}

jwt.secret=${JWT_SECRET:}
```

## 2. `application-<env>.yml`

```yaml
server:
  port: ${SERVER_PORT:4004}

auth:
  service:
    url: ${AUTH_SERVICE_URL:http://auth-service:4005}

spring:
  cloud:
    gateway:
      routes:
        - id: patient-service-route
          uri: ${PATIENT_SERVICE_URL:http://patient-service:4000}
          predicates:
            - Path=/api/patients,/api/patients/,/api/patients/**
          filters:
            - StripPrefix=1
            - JwtValidation
```

## 3. `.env.<env>`

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<db>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>

# Messaging
SPRING_KAFKA_BOOTSTRAP_SERVERS=<broker>:9092

# Internal dependencies
BILLING_SERVICE_ADDRESS=<service-host>
BILLING_SERVICE_GRPC_PORT=9090
AUTH_SERVICE_URL=http://<service-host>:4005
PATIENT_SERVICE_URL=http://<service-host>:4000

# Security
JWT_SECRET=<base64-secret>

# Runtime
SERVER_PORT=<service-port>
SPRING_PROFILES_ACTIVE=<env>
```

## 4. Runbook `docs/environments/<service>-<env>.md`

~~~markdown
# <service> - <env>

## Required Variables
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_KAFKA_BOOTSTRAP_SERVERS
- JWT_SECRET

## Dependencies
- Postgres: `<host:port>`
- Kafka: `<host:port>`
- Downstream services: `<name -> URL>`

## Start Command
~~~powershell
cd <service>
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=<env>
~~~

## Smoke Checks
- `GET /health`
- One functional endpoint for the service
~~~
