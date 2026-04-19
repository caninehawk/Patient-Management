---
name: microservices-code-analyzer
description: 'Analyze a multi-service repository to answer architecture, dependencies, and code behavior questions with project-wide context. Use for microservice system understanding, impact analysis, and onboarding.'
argument-hint: 'Question or focus area (example: auth flow, service dependencies, event contracts, API ownership)'
user-invocable: true
---

# Microservices Code Analyzer

## What This Skill Produces
- A project-wide, context-aware analysis across all microservices.
- A concise architecture summary: service roles, interfaces, data paths, and integration patterns.
- Dependency insights: service-to-service calls, shared libraries, infra dependencies, and contracts.
- Evidence-based answers to code questions with file-level references.

## When To Use
- You need to understand how the overall microservice architecture works.
- You want to answer "where does this flow start/end?" or "which services are impacted?"
- You need quick onboarding context for a large multi-service codebase.
- You need dependency and ownership analysis before changes.

## Inputs
- Primary question or analysis focus.
- Optional scope limit: all services or selected services.
- Optional depth: quick overview, medium analysis, or deep trace.

## Procedure
1. Build a service inventory.
   - Identify top-level service folders and classify each service purpose.
   - Extract language, framework, and entrypoint hints from build/config files.
2. Map interfaces and boundaries.
   - Detect external interfaces (REST, gRPC, messaging, scheduled jobs).
   - Identify where each service exposes endpoints and where it consumes others.
3. Map dependencies at three levels.
   - Code-level: imports, client wrappers, shared modules.
   - Build-level: Maven/Gradle packages, internal artifacts, plugin/tooling dependencies.
   - Runtime-level: env vars, profiles, URLs, brokers, databases, gateways.
4. Reconstruct key data and control flows.
   - Trace request paths across gateway/service layers.
   - Trace event-driven flows and contract usage (schemas/proto/messages).
5. Answer the user question using evidence.
   - Provide direct answer first.
   - Add supporting references to exact files and symbols.
   - Highlight assumptions when inferred rather than explicit.
6. Provide impact and risk context.
   - List services/components likely impacted by a change in the focused area.
   - Call out coupling risks, missing tests, config drift, and unclear ownership.

## Decision Points
- If question is broad ("explain architecture"):
  - Start with system map first, then zoom into the top 2-3 critical flows.
- If question is narrow ("where is token validated?"):
  - Prioritize deep trace in relevant services before broad inventory.
- If conflicting configurations exist across services:
  - Flag divergence explicitly and provide per-service differences.
- If contracts are implicit or missing:
  - Mark as "inferred" and identify where formal contracts should exist.
- If evidence is insufficient:
  - Return best-known answer plus exactly what additional files or runtime info are needed.

## Completion Checks
- Every substantive claim is tied to at least one file reference.
- Service interaction map includes both provider and consumer sides.
- Dependencies are separated into code/build/runtime categories.
- Unknowns and assumptions are explicitly labeled.
- Final answer includes impact summary for likely downstream changes.

## Output Template
Use this format:

```markdown
Question
- `<user question>`

Direct Answer
- `<short answer>`

Architecture Context
- Services: `<service list with responsibilities>`
- Interfaces: `<REST/gRPC/events>`
- Key Flows: `<flow 1>`, `<flow 2>`

Dependency Analysis
- Code-Level: `<details>`
- Build-Level: `<details>`
- Runtime-Level: `<details>`

Evidence
- `<path>:<line>` - `<what this proves>`
- `<path>:<line>` - `<what this proves>`

Impact Assessment
- Affected Services: `<list>`
- Risk Areas: `<list>`

Unknowns / Assumptions
- `<item>`

Confidence
- `<high|medium|low>` with reason
```

## Scope Defaults For This Repository
- Include all microservices under top-level folders such as `api-gateway`, `auth-service`, `patient-service`, `billing-service`, `analytics-service`, and `integration-tests`.
- Prefer API contracts and integration points first (gateway routes, client calls, proto/contracts, and service configs).
- Treat generated `target/` artifacts as secondary evidence unless source files are unavailable.
