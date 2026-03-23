# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

**Positive Thoughts** is a microservices demo application showcasing Red Hat OpenShift, Quarkus, and AI integration. It manages a collection of positive thoughts (quotes) with event-driven evaluation via vector embeddings.

## Build, Test, and Dev Commands

### Quarkus Services (thoughts-backend, thoughts-evaluation)

```bash
# Dev mode (auto-provisions PostgreSQL and Kafka via dev services)
cd thoughts-backend && ./mvnw quarkus:dev
cd thoughts-evaluation && ./mvnw quarkus:dev

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ThoughtResourceTest

# Run a single test method
./mvnw test -Dtest=ThoughtResourceTest#testGetAllThoughts

# Build (skip tests)
./mvnw package -DskipTests

# Run integration tests (skipped by default via <skipITs>true</skipITs>)
./mvnw verify -DskipITs=false
```

Tests use Testcontainers (requires Docker/Podman). The backend uses `smallrye-in-memory` connector for Kafka in tests. The evaluation service uses `pgvector/pgvector:pg17` image for dev/test.

### thoughts-frontend (Next.js)

```bash
cd thoughts-frontend
npm install
npm run dev          # Dev server on port 3000
npm run build        # Production build
npm test             # Jest tests
npm run test:watch   # Jest watch mode
```

### thoughts-admin-ui (Vite + React)

```bash
cd thoughts-admin-ui
npm install
npm run dev          # Vite dev server
npm run build        # Production build
npm run lint         # ESLint
npm test             # Vitest
npm run test:watch   # Vitest watch mode
```

## Service Architecture

| Service | Port | Tech | Purpose |
|---------|------|------|---------|
| `thoughts-backend` | 8080 | Quarkus | REST API, CRUD, ratings, publishes Kafka events to `thoughts.events` |
| `thoughts-evaluation` | 8088 | Quarkus | Consumes Kafka events, evaluates thoughts via vector embeddings |
| `thoughts-frontend` | 3000 | Next.js | Consumer UI - random thought display + rating |
| `thoughts-admin-ui` | 5173 | Vite/React | Admin dashboard - manage thoughts, view ratings/evaluations |

### Data Flow
```
User -> thoughts-frontend -> thoughts-backend -> PostgreSQL
                                    | (Kafka: thoughts.events)
                             thoughts-evaluation -> PostgreSQL
```

All Quarkus services share a PostgreSQL database. The evaluation service uses pgvector for cosine similarity.

## Key Patterns

- **Entities:** `PanacheEntityBase` with UUID primary keys, `@PrePersist`/`@PreUpdate` lifecycle hooks
- **REST resources:** JAX-RS `@Path`, OpenAPI annotations
- **Kafka:** Outgoing channel `thoughts-events` (backend), incoming channel `thoughts-events` (evaluation). Tests use `smallrye-in-memory` connector.
- **Quarkus profiles:** `%dev` uses dev services (auto-provisioned containers), `%test` uses Testcontainers, `%prod` reads env vars
- **Database migrations:** Flyway (`src/main/resources/db/migration/`). Evaluation service uses `clean-at-start=true` in dev/test.
- **AI embeddings:** Langchain4j OpenAI-compatible client -> Ollama (`nomic-embed-text` model) at `localhost:11434/v1` in dev
- **Frontend API client:** `NEXT_PUBLIC_API_BASE_URL` env var, defaults to `http://localhost:8080`
- **Admin UI:** Vite + React + shadcn/ui + TanStack Query + react-router-dom

## Java Package Structure

- `thoughts-backend`: `com.redhat.demos.thoughts.{model,resource,service,dto,exception,filter,health,metrics}`
- `thoughts-evaluation`: `com.redhat.demos.evaluation.{consumer,model,resource,service,dto}`

## Agent OS Workflow

Structured development via skills in `.claude/commands/agent-os/`:

1. `/plan-product` - Create/update mission, roadmap, tech stack in `agent-os/product/`
2. `/shape-spec` - Gather requirements (must be in plan mode), creates spec folder in `agent-os/specs/`
3. `/write-spec` - Create formal spec from requirements
4. `/create-tasks` - Break spec into actionable task groups
5. `/implement-tasks` or `/orchestrate-tasks` - Build the feature

Specs live in `agent-os/specs/YYYY-MM-DD-spec-name/` with `planning/requirements.md`, `spec.md`, `tasks.md`.
