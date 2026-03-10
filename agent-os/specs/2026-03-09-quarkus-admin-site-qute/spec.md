# Specification: Quarkus Admin Site with Qute

## Goal
Build a standalone Quarkus microservice that provides a server-rendered admin interface using the Qute templating engine for managing positive thoughts, viewing ratings, and displaying AI evaluation results, deployable on Red Hat OpenShift alongside the existing thoughts backend service.

## User Stories
- As a demo runner, I want a web-based admin dashboard to manage positive thoughts so that I can create, edit, and delete thoughts without using direct API calls or database access
- As a workshop attendee, I want to see how Qute provides a lightweight, server-rendered alternative to JSF/PrimeFaces and SPA frameworks so that I understand the options available in the Quarkus ecosystem

## Specific Requirements

**Separate Quarkus Service Project**
- Create a new Maven project at the repository root level (e.g., `thoughts-admin/`) separate from `thoughts-msa-ai-backend/`
- Use the same Quarkus platform BOM version 3.31.2, `groupId` of `com.redhat.demos`, and Java compiler release 25 as the existing backend service
- Include `quarkus-qute`, `quarkus-hibernate-orm-panache`, `quarkus-jdbc-postgresql`, `quarkus-rest`, `quarkus-hibernate-validator`, `quarkus-smallrye-health`, `quarkus-micrometer-registry-prometheus`, and `quarkus-kubernetes` dependencies
- Do not include Kafka-related dependencies; this service is read/write to the database only
- Use package `com.redhat.demos.thoughts.admin` to distinguish from the backend service

**Admin Dashboard Landing Page**
- Serve at root path `/` showing summary statistics: total thought count, total thumbs-up across all thoughts, total thumbs-down, and count of thoughts by status (APPROVED, REJECTED, IN_REVIEW)
- Display recent activity showing the 5 most recently created or updated thoughts
- Provide navigation links to all admin sections (Thoughts, Ratings, AI Evaluations)
- Use Panache aggregate queries (`count()`, custom HQL for sums) rather than loading all entities into memory

**Thoughts CRUD Management**
- List page at `/thoughts` displaying all thoughts in a paginated table with columns: content (truncated), author, status, thumbs up, thumbs down, created date
- Support pagination via `page` and `size` query parameters with defaults of page 0 and size 20
- Create page at `/thoughts/create` with a form including fields for content (textarea, 10-500 chars), author, and author bio, with server-side validation feedback using Hibernate Validator
- Detail page at `/thoughts/{id}` showing full thought content, all fields, associated ratings, and linked AI evaluation results if available
- Edit page at `/thoughts/{id}/edit` pre-populating a form with existing thought data, submitting via POST
- Delete via POST to `/thoughts/{id}/delete` with a confirmation step (JavaScript confirm dialog is acceptable)

**Ratings Overview Page**
- Page at `/ratings` showing a table of thoughts sorted by total rating count (thumbsUp + thumbsDown) descending
- Display columns: thought content (truncated), thumbs up count, thumbs down count, net score (thumbsUp - thumbsDown)
- Support query parameter sorting: `sort=most-liked`, `sort=most-disliked`, `sort=most-rated`

**AI Evaluation Results Display**
- Page at `/evaluations` listing all AI evaluations with pagination, replicating the pattern already established in `EvaluationUIResource.java`
- Show thought content, evaluation status (APPROVED/REJECTED), similarity score, and evaluated-at timestamp
- Support filtering by evaluation status via query parameter
- Stats sub-page at `/evaluations/stats` displaying aggregate evaluation statistics

**Qute Templating Architecture**
- Use a base layout template (`layout.html`) with `{#insert}` blocks for page title, navigation, and main content so all pages share consistent structure
- Use `@CheckedTemplate` static inner classes in resource classes for type-safe template rendering
- Place templates in `src/main/resources/templates/` following Qute naming conventions matching the resource class and method names
- Use `{#include layout}` in page templates to inherit the base layout

**Styling with Bootstrap**
- Include Bootstrap 5 CSS via CDN link in the base layout template
- Use Bootstrap grid system, table classes, form components, card components, and navigation components
- Apply responsive layout that works on desktop and tablet viewports
- Use minimal vanilla JavaScript only for confirmation dialogs and client-side filter/sort controls; no JavaScript framework

**Entity and Data Access Layer**
- Create Thought and ThoughtEvaluation entity classes mirroring the existing entities in the backend service (same table names, column names, and field types)
- Use Panache active record pattern with `PanacheEntityBase` and UUID primary keys
- Connect to the same PostgreSQL database as the thoughts backend service
- Set `quarkus.hibernate-orm.database.generation=none` for all profiles since schema is managed by the backend service
- The admin site performs both reads and writes (create, update, delete) directly to the database

**Observability and Health**
- Configure SmallRye Health with liveness and readiness probes at `/q/health/live` and `/q/health/ready`
- Add a custom health check verifying database connectivity
- Enable Micrometer Prometheus metrics at `/q/metrics`
- Configure JSON-formatted logging for production, plain text for dev

**OpenShift Deployment Configuration**
- Set `quarkus.kubernetes.deployment-target=openshift` in application.properties
- Configure datasource credentials via environment variables (`QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME`, `QUARKUS_DATASOURCE_PASSWORD`)
- Define resource requests (256Mi memory, 250m CPU) and limits (512Mi memory, 500m CPU)
- Configure liveness and readiness probe paths and timing matching the backend service pattern

## Visual Design
No visual assets provided.

## Existing Code to Leverage

**EvaluationUIResource.java (Qute pattern reference)**
- Demonstrates injecting `Template` instances and returning `TemplateInstance` from JAX-RS resource methods
- Shows pagination pattern with `page` and `size` query parameters and `hasMore` boolean for template rendering
- Provides a working example of Panache queries combined with DTO mapping for Qute template data
- The evaluations and stats templates can be directly adapted and extended for the admin site

**Thought.java and ThoughtEvaluation.java entity models**
- Provide the exact entity structure, field names, column mappings, and validation annotations to replicate in the admin service
- Show the `PanacheEntityBase` with UUID pattern, `@PrePersist`/`@PreUpdate` lifecycle hooks, and `ThoughtStatus` enum usage
- The `findRandom()` custom query method demonstrates adding static query methods to Panache entities

**Existing Qute templates (stats.html and evaluations.html)**
- Provide the established visual style (container layout, stat cards, tables, pagination, status badges) to maintain consistency
- Show Qute syntax patterns: `{#if}`, `{#for}`, `{#else}`, expression resolution, and method invocation in templates
- Demonstrate the inline CSS approach currently used, which should be replaced with Bootstrap classes in the admin site

**application.properties from thoughts-msa-ai-backend**
- Provides the exact pattern for datasource configuration, profile-based overrides, OpenShift deployment settings, health probe configuration, and resource limits
- Shows the environment variable naming conventions used for production configuration

**pom.xml from thoughts-msa-ai-backend**
- Provides the exact Quarkus BOM version, plugin configuration, and dependency structure to replicate
- Shows the `quarkus-qute` dependency is already used in the existing service, confirming compatibility

## Out of Scope
- User authentication or authorization (no login, no role-based access control)
- Real-time updates via WebSocket or Server-Sent Events
- Kafka event publishing or consuming from the admin site
- Triggering AI evaluations from the admin interface
- Public-facing consumer UI (handled by the Next.js frontend)
- API endpoints returning JSON (this service is HTML-only via Qute)
- Database schema creation or migration (managed by the backend service)
- Search or full-text filtering of thoughts
- Bulk operations (mass delete, mass status change)
- PatternFly CSS framework (use Bootstrap instead for broader familiarity in workshop settings)
