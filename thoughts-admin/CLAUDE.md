# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./mvnw compile                    # Compile
./mvnw quarkus:dev                # Dev mode with live reload (port 8088)
./mvnw test                       # Run all tests
./mvnw test -Dtest=ThoughtCrudTest # Run a single test class
./mvnw clean package              # Build production JAR
```

## What This Is

Quarkus admin dashboard for the "Positive Thoughts" Red Hat demo application. It has **no database** — all thought data comes from `thoughts-backend` (port 8080) via a MicroProfile REST Client. It serves server-rendered HTML pages using Qute templates and also exposes a JSON API at `/api/thoughts` for the separate React frontend (`thoughts-admin-frontend`).

## Architecture

**Data flow:** Browser → thoughts-admin (port 8088) → thoughts-backend (port 8080) → PostgreSQL

**Requires** `thoughts-backend` running on port 8080. The REST client URL is configured in `application.properties` via `quarkus.rest-client."...ThoughtBackendClient".url`.

### Key Packages (`com.redhat.demos.thoughts.admin`)

- **`client/`** — `ThoughtBackendClient` interface: MicroProfile REST Client proxying to `thoughts-backend`'s `/thoughts` endpoints
- **`model/`** — `Thought` Java record (DTO with validation annotations), `ThoughtStatus` enum
- **`resource/`** — JAX-RS endpoints:
  - `DashboardResource` (`/`) — Qute HTML dashboard with stats
  - `ThoughtResource` (`/thoughts`) — Qute HTML CRUD (list, detail, create, edit, delete)
  - `RatingsResource` (`/ratings`) — Qute HTML sorted ratings view
  - `ThoughtApiResource` (`/api/thoughts`) — JSON API passthrough for the React frontend
- **`filter/`** — `CorsFilter` for cross-origin requests from `localhost:8080`

### Templates

Qute templates in `src/main/resources/templates/` use `{#include layout}` inheritance. Template files map to `@CheckedTemplate` inner classes by convention: `ThoughtResource/thoughts.html` → `ThoughtResource.Templates.thoughts(...)`.

### Styling

Red Hat branded Bootstrap 5 with custom CSS at `src/main/resources/META-INF/resources/css/styles.css`. Uses Red Hat Display/Text Google Fonts, Bootstrap Icons, Red Hat red (`#EE0000`) primary color.

## Conventions

- Models are Java records, not JPA entities
- Use `@RestClient` injection for backend calls, never direct DB access
- Qute `@CheckedTemplate` with `static native` template methods for type-safe template binding
- CORS is handled via a JAX-RS `ContainerResponseFilter`, not Quarkus config properties (config-based CORS doesn't work in Quarkus 3.31.2)
