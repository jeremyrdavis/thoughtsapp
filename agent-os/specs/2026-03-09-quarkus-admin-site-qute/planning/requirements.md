# Spec Requirements: Quarkus Admin Site with Qute

## Initial Description
Build a Quarkus-based admin site using the Qute templating engine for managing positive thoughts. This replaces the need for a separate frontend framework (Next.js) with a server-rendered approach using Quarkus's native templating engine. The admin site provides CRUD management for thoughts, visibility into ratings, and administrative capabilities for the Positive Thoughts demo/workshop application.

## Context

### Existing System
- **Monolith reference**: A Jakarta EE (JBoss EAP 8.1) monolithic app exists at `monolith/monolithic-app/` using PrimeFaces/JSF for UI, Flyway for migrations, and PostgreSQL for storage
- **Thoughts Service Backend**: A Quarkus microservice already exists with REST endpoints for CRUD operations on thoughts, PostgreSQL persistence via Hibernate Panache, Kafka event publishing, and rating endpoints (thumbsup/thumbsdown)
- **Current Frontend**: A Next.js static site compiled and served by Quarkus (roadmap item #3, completed)
- **Tech Stack**: Quarkus, PostgreSQL, Kafka (AMQ Streams), Maven, Hibernate Panache, OpenShift

### Why Qute?
- Qute is Quarkus's native, type-safe templating engine designed for server-rendered HTML
- Eliminates the need for a separate Node.js build pipeline for the admin interface
- Stays within the Java/Quarkus ecosystem, making it easier for enterprise Java developers in workshop settings
- Demonstrates an alternative to SPA frameworks for admin/internal tooling
- Lighter weight than PrimeFaces/JSF while being more modern

## Requirements Summary

### Functional Requirements

**Admin Dashboard:**
- Landing page showing summary statistics (total thoughts, total ratings, recent activity)
- Navigation to all admin sections

**Thoughts Management (CRUD):**
- List all thoughts with pagination, showing content, ratings, and timestamps
- Create new thoughts via a form with validation
- View individual thought details including ratings and AI evaluation results
- Edit existing thought content
- Delete thoughts with confirmation

**Ratings Overview:**
- View rating statistics across all thoughts
- Sort/filter thoughts by rating counts (most liked, most disliked)

**AI Evaluation View:**
- Display AI evaluation results for thoughts (when available from the AI Evaluation Service)
- Show evaluation status (pending, evaluated, not evaluated)

### Technical Requirements

**Quarkus + Qute:**
- Use Quarkus Qute extension for server-side HTML rendering
- Type-safe templates with `@CheckedTemplate` annotations
- Template inheritance/layouts for consistent page structure
- Form handling with proper validation feedback

**Data Access:**
- Connect directly to the PostgreSQL database using Hibernate Panache
- Reuse or reference existing Thought entity patterns from the thoughts service backend
- UUID-based entity identity

**Styling:**
- Clean, functional admin UI (Bootstrap or PatternFly CSS framework)
- Responsive layout for desktop and tablet use
- No JavaScript framework required - use minimal vanilla JS for interactivity (confirmations, etc.)

**Observability:**
- Health endpoints for OpenShift probes
- OpenAPI documentation for any REST endpoints
- Micrometer metrics

**Deployment:**
- Deployable on Red Hat OpenShift
- Configuration via environment variables and ConfigMaps
- Kubernetes/OpenShift deployment manifests

### Scope Boundaries

**In Scope:**
- Qute-based admin UI for managing thoughts
- CRUD operations on thoughts via server-rendered forms
- Dashboard with statistics
- Ratings visibility
- AI evaluation results display
- OpenShift deployment readiness

**Out of Scope:**
- User authentication/authorization (future roadmap item)
- Real-time updates (WebSocket)
- Public-facing consumer UI (handled by Next.js frontend)
- Kafka event publishing from the admin site (admin reads data directly)
- AI evaluation triggering (handled by separate AI Evaluation Service)

### Technical Considerations

**Technology Stack:**
- Quarkus framework with Qute templating extension
- PostgreSQL for database storage
- Hibernate Panache for persistence layer
- Maven for build and dependency management
- Bootstrap or PatternFly for CSS styling
- Red Hat OpenShift for deployment

**Testing:**
- JUnit 5 with Quarkus test framework
- REST Assured for endpoint testing
- Testcontainers for integration testing with PostgreSQL

**Integration Points:**
- PostgreSQL database (shared with thoughts service)
- Read-only access pattern (admin views data, thoughts service handles writes via API)
- OpenShift platform for deployment

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
N/A - No visual files found in visuals folder.
