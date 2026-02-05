# Patient Services Platform

Multi-service Spring Boot workspace for a patient management domain. It includes REST APIs, gRPC integration, and Kafka eventing, wired together through a lightweight API gateway.

**Overview**
This repository contains multiple independent Spring Boot services that collaborate:
1. `patient-service` exposes the patient CRUD REST API and persists to Postgres.
2. `billing-service` provides a gRPC endpoint for billing account creation.
3. `analytics-service` consumes patient events from Kafka.
4. `auth-service` provides a simple login endpoint that issues JWTs.
5. `api-gateway` routes external requests to the patient API.

**Architecture**
```
Client
  |
  | HTTP :4004
  v
api-gateway (Spring Cloud Gateway)
  |  /api/patients/** -> patient-service:4000
  |  /api-docs/patients -> patient-service:4000/v3/api-docs
  v
patient-service (REST + JPA + Kafka + gRPC client)
  |  Postgres :5000
  |  Kafka topic "patient" :9092
  |  gRPC to billing-service :9090
  v
billing-service (gRPC server)

analytics-service (Kafka consumer)
auth-service (REST + JPA + JWT)
```

**Repository Layout**
1. `api-gateway` Spring Cloud Gateway on port 4004
2. `patient-service` REST API on port 4000, Postgres-backed
3. `billing-service` gRPC server on port 9090, app port 4001
4. `analytics-service` Kafka consumer on port 4002
5. `auth-service` JWT login service on port 4005

**Ports And Interfaces**
1. `api-gateway` HTTP 4004
2. `patient-service` HTTP 4000
3. `billing-service` HTTP 4001, gRPC 9090
4. `analytics-service` HTTP 4002 (no REST endpoints implemented)
5. `auth-service` HTTP 4005
6. `postgres` expected at `postgres:5000` by default
7. `auth-service-db` expected at `auth-service-db:5432` by default
8. `kafka` expected at `kafka:9092` by default

**Key Data Contracts**
1. gRPC billing API: `billing-service/src/main/proto/billing_service.proto`
2. Patient Kafka event schema: `patient-service/src/main/proto/patient_event.proto`

**API Gateway Routes**
Configured in `api-gateway/src/main/resources/application.yml`.
1. `GET/POST/PUT/DELETE /api/patients/**` -> `patient-service:4000/patients/**`
2. `GET /api-docs/patients` -> `patient-service:4000/v3/api-docs`

Note: The gateway uses Docker-style service hostnames like `patient-service`. For local runs outside Docker, update the `uri` values to `http://localhost:4000`.

**Patient Service API**
Base URL: `http://localhost:4000` (or via gateway `http://localhost:4004`).

Endpoints:
1. `GET /patients` list patients
2. `POST /patients` create patient
3. `PUT /patients/{id}` update patient
4. `DELETE /patients/{id}` delete patient

Sample request:
```json
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "address": "101 Main St, Springfield",
  "dateOfBirth": "1990-01-01",
  "registeredDate": "2024-01-10"
}
```

Swagger UI (springdoc): `http://localhost:4000/swagger-ui/index.html`
OpenAPI JSON: `http://localhost:4000/v3/api-docs`

**Auth Service API**
Base URL: `http://localhost:4005`

Endpoints:
1. `POST /login` returns a JWT on success
2. `GET /health` returns `{"status":"UP"}`
3. `GET /` returns service status

The database is seeded via `auth-service/src/main/resources/data.sql` with a single user:
1. Email: `testuser@test.com`
2. Password: stored as a BCrypt hash in `data.sql`

If you need a known password, replace the hash with one you control or insert your own user directly.

**Kafka Event Flow**
1. `patient-service` publishes `PatientEvent` messages to topic `patient`
2. `analytics-service` consumes the topic and logs the event payload

**Billing gRPC Flow**
1. `patient-service` calls `BillingService/CreateBillingAccount`
2. `billing-service` returns a dummy account id and status

Expected gRPC host and port from `patient-service/src/main/resources/application.properties`:
1. `billing.service.address=billing-service`
2. `billing.service.grpc.port=9090`

**Configuration**
All services are standard Spring Boot applications and accept overrides via environment variables.

Common overrides:
1. `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
2. `SPRING_KAFKA_BOOTSTRAP_SERVERS`
3. `BILLING_SERVICE_ADDRESS`, `BILLING_SERVICE_GRPC_PORT`
4. `JWT_SECRET` (base64-encoded secret for auth-service)

Defaults are defined in each service's `application.properties` or `application.yml`.

**Running Locally**
There is no root aggregator build. Run each service independently.

Prerequisites:
1. Java 17
2. Maven or Maven Wrapper (`mvnw`, `mvnw.cmd`)
3. Postgres for `patient-service` and `auth-service`
4. Kafka broker for `patient-service` and `analytics-service`

Start dependencies (example using Docker):
1. Start Postgres for patient-service and auth-service
2. Start Kafka and Zookeeper (or KRaft)

Adjust the following defaults to match your environment:
1. `patient-service` expects Postgres at `jdbc:postgresql://postgres:5000/db`
2. `auth-service` expects Postgres at `jdbc:postgresql://auth-service-db:5432/db`
3. Kafka at `kafka:9092`

Run services:
```powershell
cd api-gateway
.\mvnw.cmd spring-boot:run

cd ..\patient-service
.\mvnw.cmd spring-boot:run

cd ..\billing-service
.\mvnw.cmd spring-boot:run

cd ..\analytics-service
.\mvnw.cmd spring-boot:run

cd ..\auth-service
.\mvnw.cmd spring-boot:run
```

**Docker Builds**
Each service includes a `Dockerfile` that builds a runnable image.

Example:
```powershell
docker build -t patient-service ./patient-service
docker run --rm -p 4000:4000 patient-service
```

If you want services to resolve each other by name (e.g., `patient-service`, `billing-service`, `kafka`), run them on the same Docker network and keep the default hostnames or override via environment variables.

**Tests**
Run tests per service:
```powershell
cd patient-service
.\mvnw.cmd test
```

**Extending The System**
Common extension points:
1. Add gateway routes in `api-gateway/src/main/resources/application.yml`
2. Add Kafka consumers in `analytics-service`
3. Implement billing persistence or external integrations in `billing-service`
4. Expand auth APIs and add protected endpoints with Spring Security

