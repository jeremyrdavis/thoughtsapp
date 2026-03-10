# Task Breakdown: Quarkus Admin Site with Qute

## Overview
Total Tasks: 42

This spec covers building a standalone Quarkus microservice (`thoughts-admin/`) that provides a server-rendered admin interface using Qute for managing positive thoughts, viewing ratings, and displaying AI evaluation results. The service shares a PostgreSQL database with the existing `thoughts-msa-ai-backend` service but has no Kafka dependencies.

## Task List

### Project Scaffolding & Data Layer

#### Task Group 1: Maven Project and Entity Models
**Dependencies:** None

- [x] 1.0 Complete project scaffolding and entity layer
  - [x] 1.1 Write 4 focused tests for entity and data access functionality
    - Test Thought entity persistence and retrieval with Panache
    - Test ThoughtEvaluation entity persistence and association to Thought
    - Test Thought pagination via Panache `find().page()`
    - Test aggregate query for thought count by status
  - [x] 1.2 Create Maven project at `thoughts-admin/` with pom.xml
    - Use `groupId` of `com.redhat.demos`, `artifactId` of `thoughts-admin`, version `1.0`
    - Match Quarkus BOM version `3.31.2`, `maven.compiler.release` of `25`
    - Include dependencies: `quarkus-qute`, `quarkus-hibernate-orm-panache`, `quarkus-jdbc-postgresql`, `quarkus-rest`, `quarkus-hibernate-validator`, `quarkus-smallrye-health`, `quarkus-micrometer-registry-prometheus`, `quarkus-kubernetes`, `quarkus-arc`
    - Include test dependencies: `quarkus-junit`, `rest-assured`
    - Do NOT include Kafka-related dependencies
    - Replicate plugin configuration from the backend pom.xml (quarkus-maven-plugin, compiler, surefire, failsafe)
  - [x] 1.3 Create standard Quarkus directory structure
    - `src/main/java/com/redhat/demos/thoughts/admin/`
    - `src/main/resources/templates/`
    - `src/main/resources/`
    - `src/test/java/com/redhat/demos/thoughts/admin/`
  - [x] 1.4 Create `Thought` entity class in `com.redhat.demos.thoughts.admin.model`
    - Mirror field names, column names, types, and validations exactly from the backend `Thought.java`
    - Extend `PanacheEntityBase` with UUID primary key
    - Include `@PrePersist` and `@PreUpdate` lifecycle hooks
    - Include `ThoughtStatus` enum (APPROVED, REJECTED, IN_REVIEW)
    - Map to existing table `thoughts` (same table as backend service)
  - [x] 1.5 Create `ThoughtEvaluation` entity class in `com.redhat.demos.thoughts.admin.model`
    - Mirror the backend `ThoughtEvaluation.java` exactly (field names, column mappings, types)
    - Include `@ManyToOne` lazy association to `Thought`
    - Include `similarityScore` as `BigDecimal(5,4)`, `metadata` as JSONB, `evaluatedAt` timestamp
    - Map to existing table `thought_evaluations`
  - [x] 1.6 Create `application.properties` with baseline configuration
    - Set `quarkus.application.name=thoughts-admin`
    - Configure datasource: `quarkus.datasource.db-kind=postgresql`
    - Enable dev services for dev and test profiles
    - Set `quarkus.hibernate-orm.database.generation=none` for all profiles (schema managed by backend)
    - Set `%dev.quarkus.hibernate-orm.database.generation=drop-and-create` for local dev convenience
    - Configure prod datasource via environment variables (`QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME`, `QUARKUS_DATASOURCE_PASSWORD`)
  - [x] 1.7 Add `import.sql` for dev profile test data
    - Insert sample thoughts with varying statuses, ratings, authors
    - Insert sample thought evaluations linked to thoughts
  - [x] 1.8 Ensure entity layer tests pass
    - Run ONLY the 4 tests written in 1.1
    - Verify entities persist and query correctly via Quarkus dev services PostgreSQL

**Acceptance Criteria:**
- Maven project builds successfully with `./mvnw compile`
- All 4 entity tests pass
- Thought and ThoughtEvaluation entities mirror the backend service exactly
- Dev services spin up PostgreSQL automatically for dev/test profiles
- No Kafka dependencies present

---

### Qute Templating Foundation

#### Task Group 2: Base Layout and Dashboard
**Dependencies:** Task Group 1

- [x] 2.0 Complete base layout and admin dashboard
  - [x] 2.1 Write 3 focused tests for dashboard functionality
    - Test GET `/` returns 200 with HTML content type
    - Test dashboard response contains summary statistics (total count, thumbs up/down sums)
    - Test dashboard shows recent activity section with up to 5 thoughts
  - [x] 2.2 Create base layout template at `templates/layout.html`
    - Include Bootstrap 5 CSS via CDN link in `<head>`
    - Define `{#insert title}` block for page title
    - Define `{#insert content}` block for main page content
    - Add responsive navigation bar with links: Dashboard (`/`), Thoughts (`/thoughts`), Ratings (`/ratings`), Evaluations (`/evaluations`)
    - Use Bootstrap navbar component with collapse for mobile
    - Include minimal viewport meta tag for responsive behavior
  - [x] 2.3 Create `DashboardResource` in `com.redhat.demos.thoughts.admin.resource`
    - Use `@CheckedTemplate` static inner class for type-safe template rendering
    - Serve at `@Path("/")` with `@Produces(MediaType.TEXT_HTML)`
    - GET `/` returns dashboard template instance
    - Compute summary stats using Panache aggregate queries: `Thought.count()`, HQL for `SUM(thumbsUp)`, `SUM(thumbsDown)`, count by status
    - Query 5 most recently updated thoughts for recent activity section
  - [x] 2.4 Create dashboard template at `templates/DashboardResource/dashboard.html`
    - Use `{#include layout}` to inherit base layout
    - Display stat cards (Bootstrap card component) for: total thoughts, total thumbs up, total thumbs down
    - Display status breakdown: count of APPROVED, REJECTED, IN_REVIEW thoughts
    - Display recent activity table with columns: content (truncated to 50 chars), author, status, updated date
    - Each recent thought links to its detail page `/thoughts/{id}`
  - [x] 2.5 Ensure dashboard tests pass
    - Run ONLY the 3 tests written in 2.1

**Acceptance Criteria:**
- Dashboard loads at `/` with consistent navigation
- Summary statistics display correctly using aggregate queries (not loading all entities)
- Recent activity shows up to 5 most recently updated thoughts
- Base layout is shared and renders Bootstrap-styled navigation
- All 3 tests pass

---

### Thoughts CRUD

#### Task Group 3: Thoughts List and Detail Pages
**Dependencies:** Task Group 2

- [x] 3.0 Complete thoughts list and detail views
  - [x] 3.1 Write 4 focused tests for thoughts list and detail endpoints
    - Test GET `/thoughts` returns 200 with paginated thought table
    - Test GET `/thoughts?page=1&size=5` respects pagination parameters
    - Test GET `/thoughts/{id}` returns 200 with full thought detail
    - Test GET `/thoughts/{id}` for non-existent UUID returns 404 or appropriate error
  - [x] 3.2 Create `ThoughtResource` in `com.redhat.demos.thoughts.admin.resource`
    - Use `@CheckedTemplate` static inner class
    - Serve at `@Path("/thoughts")` with `@Produces(MediaType.TEXT_HTML)`
    - GET `/thoughts` with `@QueryParam("page")` default 0 and `@QueryParam("size")` default 20
    - Paginate using `Thought.findAll().page(page, size).list()`
    - Compute `hasMore` boolean and total count for pagination controls
    - GET `/thoughts/{id}` fetching thought by UUID, including associated evaluations via query
  - [x] 3.3 Create thoughts list template at `templates/ThoughtResource/thoughts.html`
    - Use `{#include layout}` for base layout
    - Render paginated table with columns: content (truncated to 80 chars), author, status, thumbs up, thumbs down, created date
    - Status column uses Bootstrap badge classes (green for APPROVED, red for REJECTED, yellow for IN_REVIEW)
    - Each row links to detail page `/thoughts/{id}`
    - Include "Create New Thought" button linking to `/thoughts/create`
    - Render pagination controls (Previous/Next) with correct page query parameters
  - [x] 3.4 Create thought detail template at `templates/ThoughtResource/detail.html`
    - Display full thought content, author, author bio, status, thumbs up, thumbs down, created and updated dates
    - Display associated evaluations if any (status, similarity score, evaluated at)
    - Include Edit button linking to `/thoughts/{id}/edit`
    - Include Delete button (form with POST to `/thoughts/{id}/delete`) with JavaScript confirm dialog
    - Include Back link to `/thoughts`
  - [x] 3.5 Ensure thoughts list and detail tests pass
    - Run ONLY the 4 tests written in 3.1

**Acceptance Criteria:**
- Thoughts list displays paginated data with correct column content
- Pagination controls navigate between pages correctly
- Thought detail page shows all fields and associated evaluations
- Status badges use appropriate Bootstrap color classes
- All 4 tests pass

---

#### Task Group 4: Thoughts Create, Edit, and Delete
**Dependencies:** Task Group 3

- [x] 4.0 Complete thoughts write operations
  - [x] 4.1 Write 5 focused tests for create, edit, and delete operations
    - Test GET `/thoughts/create` returns 200 with form
    - Test POST `/thoughts/create` with valid data creates thought and redirects to detail page
    - Test POST `/thoughts/create` with invalid data (content too short) returns form with validation errors
    - Test GET `/thoughts/{id}/edit` returns 200 with pre-populated form
    - Test POST `/thoughts/{id}/delete` deletes the thought and redirects to `/thoughts`
  - [x] 4.2 Add create endpoints to `ThoughtResource`
    - GET `/thoughts/create` renders create form template
    - POST `/thoughts/create` with `@FormParam` for content, author, authorBio
    - Validate using Hibernate Validator; on failure, re-render form with error messages and submitted values
    - On success, persist thought via `thought.persist()` in a `@Transactional` method, redirect to `/thoughts/{id}`
  - [x] 4.3 Create thought create template at `templates/ThoughtResource/create.html`
    - Form with fields: content (textarea, required, 10-500 chars), author (text input), authorBio (text input)
    - Display validation error messages next to each field using Bootstrap `is-invalid` and `invalid-feedback` classes
    - Preserve submitted values on validation failure
    - Submit via POST to `/thoughts/create`
    - Include Cancel link back to `/thoughts`
  - [x] 4.4 Add edit endpoints to `ThoughtResource`
    - GET `/thoughts/{id}/edit` fetches thought and renders edit form pre-populated with existing data
    - POST `/thoughts/{id}/edit` with `@FormParam` fields, validates, updates entity, redirects to detail page
    - Handle not-found case for invalid UUID
  - [x] 4.5 Create thought edit template at `templates/ThoughtResource/edit.html`
    - Same form structure as create template but pre-populated with existing thought values
    - Submit via POST to `/thoughts/{id}/edit`
    - Include Cancel link back to `/thoughts/{id}`
  - [x] 4.6 Add delete endpoint to `ThoughtResource`
    - POST `/thoughts/{id}/delete` deletes the thought in a `@Transactional` method
    - Redirect to `/thoughts` after deletion
    - Handle not-found case
  - [x] 4.7 Ensure thoughts CRUD tests pass
    - Run ONLY the 5 tests written in 4.1

**Acceptance Criteria:**
- Create form validates input and shows errors inline using Hibernate Validator
- Successfully created thoughts appear in the list and can be viewed on detail page
- Edit form pre-populates with existing data and saves changes
- Delete removes the thought after confirmation and redirects to list
- All 5 tests pass

---

### Ratings & Evaluations Views

#### Task Group 5: Ratings Overview and AI Evaluations Pages
**Dependencies:** Task Group 2

- [x] 5.0 Complete ratings and evaluations pages
  - [x] 5.1 Write 5 focused tests for ratings and evaluations endpoints
    - Test GET `/ratings` returns 200 with thought rating table
    - Test GET `/ratings?sort=most-liked` sorts by thumbs up descending
    - Test GET `/evaluations` returns 200 with paginated evaluations
    - Test GET `/evaluations?status=APPROVED` filters evaluations by status
    - Test GET `/evaluations/stats` returns 200 with aggregate statistics
  - [x] 5.2 Create `RatingsResource` in `com.redhat.demos.thoughts.admin.resource`
    - Serve at `@Path("/ratings")` with `@Produces(MediaType.TEXT_HTML)`
    - Use `@CheckedTemplate` for type-safe templates
    - GET `/ratings` with `@QueryParam("sort")` defaulting to `most-rated`
    - Query thoughts with custom HQL ordering: `most-liked` orders by `thumbsUp DESC`, `most-disliked` by `thumbsDown DESC`, `most-rated` by `(thumbsUp + thumbsDown) DESC`
  - [x] 5.3 Create ratings template at `templates/RatingsResource/ratings.html`
    - Use `{#include layout}` for base layout
    - Display sort controls as Bootstrap button group or dropdown linking to `?sort=most-liked`, `?sort=most-disliked`, `?sort=most-rated`
    - Highlight active sort option
    - Table columns: thought content (truncated), thumbs up, thumbs down, net score (thumbsUp - thumbsDown)
    - Each row links to thought detail page
  - [x] 5.4 Create `EvaluationResource` in `com.redhat.demos.thoughts.admin.resource`
    - Serve at `@Path("/evaluations")` with `@Produces(MediaType.TEXT_HTML)`
    - Use `@CheckedTemplate` for type-safe templates
    - GET `/evaluations` with `page`, `size`, and optional `status` query parameters
    - Filter by `ThoughtStatus` if `status` param is provided
    - Compute `hasMore` and total count for pagination
    - Adapt the pattern from the existing `EvaluationUIResource.java`
    - GET `/evaluations/stats` computing aggregate stats: total evaluated, approved count, rejected count, average similarity score
  - [x] 5.5 Create evaluations list template at `templates/EvaluationResource/evaluations.html`
    - Use `{#include layout}` for base layout
    - Status filter controls: links for All, APPROVED, REJECTED
    - Table columns: thought content (truncated), evaluation status (with Bootstrap badge), similarity score, evaluated at timestamp
    - Pagination controls (Previous/Next)
  - [x] 5.6 Create evaluations stats template at `templates/EvaluationResource/stats.html`
    - Display stat cards: total evaluations, approved count, rejected count, average similarity score
    - Link back to evaluations list
  - [x] 5.7 Ensure ratings and evaluations tests pass
    - Run ONLY the 5 tests written in 5.1

**Acceptance Criteria:**
- Ratings page displays thoughts sorted by selected sort criteria
- Sort controls switch between most-liked, most-disliked, and most-rated
- Evaluations page displays paginated evaluations with status filter
- Evaluations stats page shows correct aggregate statistics
- All 5 tests pass

---

### Observability & Deployment

#### Task Group 6: Health Checks, Metrics, and OpenShift Configuration
**Dependencies:** Task Group 1

- [x] 6.0 Complete observability and deployment configuration
  - [x] 6.1 Write 3 focused tests for health and metrics endpoints
    - Test GET `/q/health/live` returns 200 with UP status
    - Test GET `/q/health/ready` returns 200 with UP status
    - Test GET `/q/metrics` returns 200 with Prometheus format content
  - [x] 6.2 Create custom database health check
    - Implement `org.eclipse.microprofile.health.HealthCheck` in `com.redhat.demos.thoughts.admin.health`
    - Name it `DatabaseConnectionHealthCheck`
    - Annotate with `@Readiness`
    - Verify database connectivity by executing a simple query
  - [x] 6.3 Configure logging in `application.properties`
    - Set `quarkus.log.console.json=true` for production (default)
    - Set `%dev.quarkus.log.console.json=false` for dev profile
    - Set `%test.quarkus.log.console.json=false` for test profile
  - [x] 6.4 Configure OpenShift deployment in `application.properties`
    - Set `quarkus.kubernetes.deployment-target=openshift`
    - Add OpenShift labels: `app.kubernetes.io/name=thoughts-admin`, `app.kubernetes.io/version=1.0.0`, `app.openshift.io/runtime=quarkus`
    - Set resource requests: 256Mi memory, 250m CPU
    - Set resource limits: 512Mi memory, 500m CPU
    - Configure liveness probe: path `/q/health/live`, initial delay 30s, period 10s
    - Configure readiness probe: path `/q/health/ready`, initial delay 10s, period 5s
    - Configure container image settings: group `redhat-demos`, name `thoughts-admin`, tag `latest`
  - [x] 6.5 Ensure observability tests pass
    - Run ONLY the 3 tests written in 6.1

**Acceptance Criteria:**
- Health endpoints respond correctly with database connectivity check
- Prometheus metrics endpoint is available
- OpenShift deployment configuration generates correct manifests
- Logging uses JSON in production, plain text in dev
- All 3 tests pass

---

### Testing

#### Task Group 7: Test Review and Gap Analysis
**Dependencies:** Task Groups 1-6

- [x] 7.0 Review existing tests and fill critical gaps only
  - [x] 7.1 Review tests from Task Groups 1-6
    - Review the 4 entity tests from Task Group 1
    - Review the 3 dashboard tests from Task Group 2
    - Review the 4 thoughts list/detail tests from Task Group 3
    - Review the 5 thoughts CRUD tests from Task Group 4
    - Review the 5 ratings/evaluations tests from Task Group 5
    - Review the 3 observability tests from Task Group 6
    - Total existing tests: 24 tests
  - [x] 7.2 Analyze test coverage gaps for this feature only
    - Identify critical user workflows that lack coverage
    - Focus on integration between CRUD operations and template rendering
    - Assess whether navigation flow between pages is tested
    - Check that pagination edge cases (empty results, single page) are minimally covered
  - [x] 7.3 Write up to 10 additional strategic tests maximum
    - Add tests to fill identified critical gaps only
    - Prioritize end-to-end workflows: create thought then verify it appears in list, delete thought then verify it is removed
    - Test navigation links render correctly in layout
    - Test that dashboard statistics update after creating/deleting a thought
    - Do NOT write exhaustive edge case or accessibility tests
  - [x] 7.4 Run all feature-specific tests
    - Run full test suite via `./mvnw test` within `thoughts-admin/`
    - Expected total: approximately 24-34 tests
    - Verify all tests pass

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 24-34 tests total)
- Critical user workflows (create, read, update, delete, navigate) are covered
- No more than 10 additional tests added
- Testing focused exclusively on this spec's feature requirements

---

## Execution Order

Recommended implementation sequence:

1. **Task Group 1** (Maven Project and Entity Models) - Foundation: project structure, dependencies, entities, configuration
2. **Task Group 2** (Base Layout and Dashboard) - Qute templating foundation, navigation, dashboard with aggregate queries
3. **Task Group 3** (Thoughts List and Detail) - Read-only views with pagination and detail display
4. **Task Group 4** (Thoughts Create, Edit, Delete) - Write operations with form handling and validation
5. **Task Group 5** (Ratings and Evaluations) - Read-only views that can be built in parallel with Task Group 4 (both depend only on Task Group 2)
6. **Task Group 6** (Health, Metrics, OpenShift) - Can be built in parallel with Task Groups 3-5 (depends only on Task Group 1)
7. **Task Group 7** (Test Review and Gap Analysis) - Final test review after all features are complete

**Parallelization opportunities:**
- Task Groups 3 and 5 can run in parallel (both depend on Task Group 2, no dependency on each other)
- Task Group 6 can run in parallel with Task Groups 3, 4, and 5 (depends only on Task Group 1)
- Task Group 4 must follow Task Group 3 (extends the same resource class)
