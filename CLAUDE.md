# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

**Positive Thoughts** is a microservices demonstration application that helps enterprise Java developers and solutions architects understand modern cloud-native development. It showcases Red Hat OpenShift, Quarkus, and AI integration through an interactive workshop and demo experience.

This repository contains both the application microservices and an Agent OS configuration for structured development workflows.

## Directory Structure

```
DevHub/
├── thoughts-backend/          # Quarkus backend REST API (CRUD, ratings, Kafka events)
│   ├── src/main/java/com/redhat/demos/thoughts/
│   │   ├── model/             # Thought, ThoughtEvaluation, ThoughtStatus entities
│   │   ├── resource/          # ThoughtResource (REST), EvaluationUIResource (Qute)
│   │   ├── service/           # ThoughtEventService (Kafka publishing)
│   │   ├── dto/               # EvaluationDTO, EvaluationStatsDTO
│   │   ├── exception/         # Exception mappers
│   │   ├── filter/            # CorsFilter
│   │   ├── health/            # Database, Kafka, LLM health checks
│   │   └── metrics/           # EvaluationMetrics
│   └── src/main/resources/
│       ├── templates/         # Qute templates (evaluations.html, stats.html)
│       ├── db/migration/      # Flyway migrations (V1-V5)
│       └── import.sql         # Seed data
│
├── thoughts-frontend/         # Next.js consumer UI (static site, shadcn/ui)
│   ├── app/                   # page.tsx (random thought display + rating)
│   ├── lib/                   # api-client.ts, types.ts
│   └── components/ui/         # shadcn/ui components (button, skeleton, sonner)
│
├── thoughts-evaluation/       # Quarkus AI evaluation service
│   ├── src/main/java/com/redhat/demos/evaluation/
│   │   ├── consumer/          # ThoughtEvaluationConsumer (Kafka)
│   │   ├── model/             # ThoughtEvaluation, EvaluationVector, VectorType
│   │   ├── resource/          # EvaluationResource (REST)
│   │   ├── service/           # EvaluationService, EmbeddingService, VectorSimilarityService
│   │   └── dto/               # EvaluationDTO, EvaluationStatsDTO, ThoughtEvent
│   └── src/main/resources/
│       └── db/migration/      # Flyway migrations (V1-V3)
│
├── thoughts-admin/            # Quarkus admin UI with Qute templating (NEW)
│   ├── src/main/java/com/redhat/demos/thoughts/admin/
│   │   ├── model/             # Thought, ThoughtEvaluation, ThoughtStatus (mirrors backend)
│   │   ├── resource/          # DashboardResource, ThoughtResource, RatingsResource, EvaluationResource
│   │   └── health/            # DatabaseConnectionHealthCheck
│   └── src/main/resources/
│       ├── templates/         # Qute templates (layout.html + per-resource templates)
│       └── import.sql         # Dev seed data
│
├── infrastructure/            # OpenShift deployment configs
│   ├── kafka/                 # AMQ Streams operator, cluster, topics (YAML + setup.sh)
│   └── postgresql/            # PostgreSQL secret, PVC, deployment, service (YAML + setup.sh)
│
├── agent-os/                  # Agent OS configuration
│   ├── config.yml             # Agent OS version and settings
│   ├── product/               # mission.md, roadmap.md, tech-stack.md
│   ├── standards/             # Coding standards (global/, backend/, frontend/, testing/)
│   └── specs/                 # Feature specifications
│       ├── 2026-02-04-thoughts-service-backend/
│       ├── 2026-02-04-frontend-application/
│       ├── 2026-02-05-add-author-bio-to-quotes/
│       ├── 2026-02-09-ai-evaluation-service/
│       └── 2026-03-09-quarkus-admin-site-qute/
│
├── .claude/                   # Claude Code agent and command definitions
│   ├── commands/agent-os/     # Skill definitions (/shape-spec, /write-spec, etc.)
│   └── agents/agent-os/       # Subagent definitions (spec-writer, implementer, etc.)
│
├── quotes.json                # Raw quotes data
├── quotes_transformed.json    # Transformed quotes for import
└── README.md
```

## Tech Stack

- **Backend framework:** Quarkus (Java 25, Maven)
- **Frontend:** Next.js + shadcn/ui + Tailwind CSS (compiled to static site)
- **Admin UI:** Quarkus Qute templating + Bootstrap 5
- **Database:** PostgreSQL (Hibernate Panache, Flyway migrations)
- **Messaging:** Apache Kafka (Red Hat AMQ Streams)
- **AI:** Vector similarity evaluation via embedding service
- **Deployment:** Red Hat OpenShift (Kubernetes manifests)
- **Testing:** JUnit 5, REST Assured, Testcontainers

## Service Architecture

| Service | Port | Purpose | Database | Kafka |
|---------|------|---------|----------|-------|
| `thoughts-backend` | 8080 | REST API for thoughts CRUD, ratings, Kafka events | PostgreSQL (read/write, schema owner) | Publishes to `thoughts.events` |
| `thoughts-frontend` | 3000 | Consumer UI - displays random thoughts, collects ratings | None (API client) | None |
| `thoughts-evaluation` | 8082 | AI-powered thought evaluation via vector similarity | PostgreSQL (read/write) | Consumes from `thoughts.events` |
| `thoughts-admin` | 8081 | Admin dashboard - manage thoughts, view ratings/evaluations | PostgreSQL (read/write, no schema mgmt) | None |

### Data Flow
```
User → thoughts-frontend → thoughts-backend → PostgreSQL
                                    ↓ (Kafka)
                           thoughts-evaluation → PostgreSQL

Admin → thoughts-admin → PostgreSQL (direct, shared DB)
```

## Key Patterns

- **Entities:** `PanacheEntityBase` with UUID primary keys, `@PrePersist`/`@PreUpdate` lifecycle hooks
- **REST resources:** JAX-RS with `@Path`, OpenAPI documentation
- **Qute templates:** `@CheckedTemplate` static inner classes, `{#include layout}` inheritance
- **Configuration:** Environment variables for prod, dev services for dev/test profiles
- **Health:** SmallRye Health (liveness + readiness), Micrometer Prometheus metrics
- **Frontend API client:** `NEXT_PUBLIC_API_BASE_URL` env var, defaults to `http://localhost:8080`

## Agent OS Workflow

1. **Product Planning** (`/plan-product`) - Creates mission, roadmap, tech stack docs
2. **Spec Shaping** (`/shape-spec`) - Gathers requirements through questions
3. **Spec Writing** (`/write-spec`) - Creates formal spec from requirements
4. **Task Creation** (`/create-tasks`) - Breaks spec into actionable task groups
5. **Implementation** (`/implement-tasks` or `/orchestrate-tasks`) - Builds the feature

Each spec lives in `agent-os/specs/YYYY-MM-DD-spec-name/` with `planning/requirements.md`, `spec.md`, `tasks.md`, and `verifications/final-verification.md`.
