---
name: service-environment-bootstrap
description: 'Create or extend per-service environments (dev, qa, prod) in this Spring Boot microservices repo. Use for environment onboarding, profile file creation, env var mapping, and rollout checks when asked to add a new environment for any service.'
argument-hint: 'Service name + target environment (example: patient-service qa)'
user-invocable: true
---

# Service Environment Bootstrap

## What This Skill Produces
- A repeatable environment setup plan for one service and one target environment (`dev`, `qa`, or `prod`).
- Concrete file operations with exact paths and suggested content blocks.
- Environment-specific Spring profile configuration files and runtime variable templates.
- A validation checklist to confirm the new environment can start safely.

## When To Use
- You are asked to create a new environment for a specific service.
- A service already runs in one environment and needs `dev`/`qa`/`prod` parity.
- You need a consistent way to add environment files without editing unrelated services.

## Inputs
- `service`: One of `api-gateway`, `auth-service`, `patient-service`, `billing-service`, `analytics-service`, or another valid service folder.
- `environment`: `dev`, `qa`, or `prod`.
- Optional: source environment to copy from (`dev` -> `qa`, `qa` -> `prod`).
- Optional: deployment target assumptions (local Docker, CI/CD, Kubernetes, VM).

## Procedure
1. Discover service config style.
   - Open `<service>/src/main/resources/`.
   - Detect base config format: `application.properties` or `application.yml`.
   - Detect existing profile files: `application-*.properties` or `application-*.yml`.
2. Build an environment delta.
   - Compare the target environment with base/default values.
   - Mark values that must differ by environment: database URL, credentials, Kafka brokers, service hostnames, JWT secrets, ports, logging levels.
3. Create or update Spring profile config.
   - If service uses properties, create `<service>/src/main/resources/application-<environment>.properties`.
   - If service uses YAML, create `<service>/src/main/resources/application-<environment>.yml`.
   - Keep only environment-specific overrides in profile file; avoid copying unchanged defaults.
4. Create runtime variable file for deployment.
   - Create `<service>/.env.<environment>`.
   - Map sensitive and infra-specific values to environment variables (for example `SPRING_DATASOURCE_URL`, `SPRING_KAFKA_BOOTSTRAP_SERVERS`, `JWT_SECRET`, `BILLING_SERVICE_ADDRESS`).
5. Add environment runbook entry.
   - Create or update `docs/environments/<service>-<environment>.md` with:
     - required variables
     - dependency endpoints
     - startup command
     - smoke-test endpoints
6. Validate startup and behavior.
   - Ensure the service starts with `--spring.profiles.active=<environment>`.
   - Verify required dependencies resolve (DB/Kafka/downstream service).
   - Confirm one minimal health or smoke check succeeds.

## Decision Points
- If `application.yml` is primary:
  - Create `application-<environment>.yml` instead of `.properties`.
- If profile file already exists:
  - Update only missing or incorrect keys; do not rewrite wholesale.
- If target environment is `prod`:
  - Do not store secrets directly in repo files; require env variables or secret manager references.
- If service depends on other internal services:
  - Require environment-correct hostnames/URLs for each dependency.
- If no clear baseline exists:
  - Clone from nearest environment (`dev` -> `qa`, `qa` -> `prod`) and then adjust only true deltas.

## Completion Checks
- Profile file exists for target environment in the service resources folder.
- `.env.<environment>` exists with all required variable keys.
- Secrets are not committed as plaintext in `prod` profile files.
- Runbook file exists in `docs/environments/` and documents startup + smoke tests.
- Service starts with target profile and passes at least one smoke check.

## Output Template
Use this format:

```markdown
Environment Request
- Service: `<service>`
- Target Environment: `<environment>`
- Baseline Source: `<base env or default>`

Files Created/Updated
- `<path>` - `<created|updated>` - `<purpose>`
- `<path>` - `<created|updated>` - `<purpose>`

Config Delta
- `<key>`: `<old/default>` -> `<new for env>`
- `<key>`: `<old/default>` -> `<new for env>`

Validation
- Startup: `<command and result>`
- Smoke test: `<endpoint/check and result>`

Risks/Follow-ups
- `<missing secret>`
- `<dependency not reachable>`
```

## Repository Defaults
- Default service ports from repository docs:
  - `api-gateway`: `4004`
  - `patient-service`: `4000`
  - `billing-service`: `4001` (HTTP), `9090` (gRPC)
  - `analytics-service`: `4002`
  - `auth-service`: `4005`
- Common environment-sensitive keys to review:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
  - `SPRING_KAFKA_BOOTSTRAP_SERVERS`
  - `BILLING_SERVICE_ADDRESS`
  - `BILLING_SERVICE_GRPC_PORT`
  - `JWT_SECRET`

## References
- [Environment file templates](./references/environment-file-templates.md)
