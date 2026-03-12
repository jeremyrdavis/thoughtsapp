# Task Breakdown: Thoughts Admin Qute Template Rebuild

## Overview
Total Tasks: 18

This spec involves targeted CSS and template changes to make the Qute-rendered admin site visually match the React-based frontend. There are no database, model, or backend logic changes. The work is organized into three groups: CSS updates, template updates, and verification.

## Task List

### CSS Layer

#### Task Group 1: styles.css Updates
**Dependencies:** None

- [x] 1.0 Complete all CSS changes in `styles.css`
  - [x] 1.1 Write 4 focused tests to verify CSS-dependent rendering
    - Test that the application starts and serves the dashboard page
    - Test that `/ratings` page loads successfully
    - Test that `/evaluations` page loads successfully
    - Test that `styles.css` is served at `/css/styles.css`
  - [x] 1.2 Change table link color from red to near-black
    - In `styles.css`, change `.data-table a` color from `var(--rh-red)` to `var(--rh-black)`
    - Keep existing `text-decoration: none` default and `text-decoration: underline` on hover
  - [x] 1.3 Replace thumbs up/down bold classes with plain color utilities
    - Remove `.text-success-bold` definition (color + font-weight: 600)
    - Remove `.text-destructive-bold` definition (color + font-weight: 600)
    - Add `.text-success { color: var(--success); }` utility class
    - Add `.text-destructive { color: var(--destructive); }` utility class
  - [x] 1.4 Remove sort button pill CSS rule
    - Remove the `.sort-group .btn.rounded-pill` rule (padding-left/right overrides)
    - Keep the `.sort-group .btn` font-size rule
  - [x] 1.5 Add container max-width media query
    - Add `@media (min-width: 1200px) { .container { max-width: 1400px; } }`
    - Place it in the Content Area section of the stylesheet
  - [x] 1.6 Ensure CSS tests pass
    - Run ONLY the 4 tests written in 1.1
    - Verify `styles.css` is syntactically valid and served correctly

**Acceptance Criteria:**
- The 4 tests from 1.1 pass
- `.data-table a` uses `var(--rh-black)` color
- `.text-success` and `.text-destructive` are plain color-only utilities (no font-weight)
- Old `.text-success-bold` and `.text-destructive-bold` classes are removed
- `.sort-group .btn.rounded-pill` rule is removed
- Container max-width is 1400px at the 1200px breakpoint

**Reference visuals:**
- `planning/visuals/thoughts.png` - near-black table links
- `planning/visuals/ratings.png` - rectangular sort buttons

### Template Layer

#### Task Group 2: Qute Template Updates
**Dependencies:** Task Group 1 (CSS class renames must be in place)

- [x] 2.0 Complete all Qute template changes
  - [x] 2.1 Write 4 focused tests to verify template rendering
    - Test that dashboard page contains "Recent Activity" heading
    - Test that thoughts list page renders the data table
    - Test that ratings page renders sort buttons without `rounded-pill` class
    - Test that thought detail page renders thumbs up/down values
  - [x] 2.2 Update `dashboard.html` heading spacing
    - Change `<h2 class="mb-3">Recent Activity</h2>` to `<h2 class="mb-4">Recent Activity</h2>`
    - No other changes to this template
  - [x] 2.3 Update `thoughts.html` thumbs class names
    - Change `text-success-bold` to `text-success` on thumbs up column
    - Change `text-destructive-bold` to `text-destructive` on thumbs down column
  - [x] 2.4 Update `ratings.html` sort buttons and thumbs class names
    - Remove `rounded-pill` from all three sort `<a>` tags
    - Change `gap-2` to `gap-1` on the sort-group `div`
    - Change `text-success-bold` to `text-success` on thumbs up column
    - Change `text-destructive-bold` to `text-destructive` on thumbs down column
  - [x] 2.5 Update `detail.html` thumbs class names
    - Change `text-success-bold fs-4` to `text-success fw-bold fs-4` on thumbs up value
    - Change `text-destructive-bold fs-4` to `text-destructive fw-bold fs-4` on thumbs down value
    - This preserves bold weight on the detail page per the React pattern (`text-xl font-bold`)
  - [x] 2.6 Update `evaluations.html` button styling
    - Change `btn-outline-primary btn-sm` to `btn-outline-secondary` on the "View Statistics" button
    - Remove `btn-sm` so the button uses default sizing
  - [x] 2.7 Update `stats.html` button sizing
    - Remove `btn-sm` from the "Back to Evaluations" button (keep `btn-outline-secondary`)
  - [x] 2.8 Ensure template tests pass
    - Run ONLY the 4 tests written in 2.1
    - Verify all template changes render correctly

**Acceptance Criteria:**
- The 4 tests from 2.1 pass
- Dashboard "Recent Activity" heading uses `mb-4` spacing
- All `text-success-bold` / `text-destructive-bold` references are replaced across thoughts, ratings, and detail templates
- Ratings sort buttons are rectangular (no `rounded-pill`) with `gap-1`
- Detail page thumbs values use `text-success fw-bold fs-4` / `text-destructive fw-bold fs-4`
- Evaluations "View Statistics" button uses `btn-outline-secondary` at default size
- Stats "Back to Evaluations" button uses default size (no `btn-sm`)
- All existing Qute data binding, conditionals, and `{#include layout}` inheritance remain intact

**Reference visuals:**
- `planning/visuals/dashboard.png` - heading spacing
- `planning/visuals/ratings.png` - rectangular buttons, gap
- `planning/visuals/thought_detail.png` - bold thumbs values
- `planning/visuals/evaluations.png` - secondary outline button

#### Task Group 3: Evaluation Files Git Tracking
**Dependencies:** Task Group 2

- [x] 3.0 Ensure evaluation files are tracked in git
  - [x] 3.1 Stage `EvaluationResource.java` so it is tracked
    - File exists on disk at `src/main/java/com/redhat/demos/thoughts/admin/resource/EvaluationResource.java`
    - Currently shows as untracked (`??`) in git status
    - Stage the file with `git add`
  - [x] 3.2 Stage evaluation template files so they are tracked
    - `src/main/resources/templates/EvaluationResource/evaluations.html` - currently untracked
    - `src/main/resources/templates/EvaluationResource/stats.html` - currently untracked
    - Stage both files with `git add`
  - [x] 3.3 Verify all evaluation files are staged and the app compiles
    - Run `git status` to confirm files are staged
    - Run `./mvnw compile` to verify the project builds with all files included

**Acceptance Criteria:**
- `EvaluationResource.java` is tracked in git
- `evaluations.html` and `stats.html` are tracked in git
- The project compiles successfully with all evaluation files included
- The `/evaluations` and `/evaluations/stats` routes are functional

### Verification

#### Task Group 4: Visual Verification and Test Review
**Dependencies:** Task Groups 1-3

- [x] 4.0 Review all changes and verify visual match
  - [x] 4.1 Review tests from Task Groups 1 and 2
    - Review the 4 tests written in Task 1.1
    - Review the 4 tests written in Task 2.1
    - Total existing tests: 8
  - [x] 4.2 Analyze test coverage gaps for this feature
    - Identify any critical rendering paths not covered
    - Focus on integration between CSS changes and template changes
    - Verify the evaluation pages are accessible end-to-end
  - [x] 4.3 Write up to 5 additional verification tests
    - Test that table links do NOT contain `color: var(--rh-red)` in served CSS
    - Test that evaluations page button has `btn-outline-secondary` class
    - Test that ratings page sort buttons do NOT have `rounded-pill` class in rendered HTML
    - Test that detail page thumbs up uses `text-success fw-bold` classes
    - Test that dashboard "Recent Activity" heading has `mb-4` class
  - [x] 4.4 Run all feature-specific tests
    - Run ONLY the tests related to this spec (tests from 1.1, 2.1, and 4.3)
    - Expected total: approximately 13 tests
    - Verify all pass

**Acceptance Criteria:**
- All 13 feature-specific tests pass
- CSS changes produce the intended visual result (table links near-black, no pill buttons, wider container)
- Template class references are consistent with CSS class definitions
- No broken templates or missing class references
- Evaluation pages load and render correctly

## Execution Order

Recommended implementation sequence:
1. CSS Layer (Task Group 1) - Update `styles.css` first since templates depend on new class names
2. Template Layer (Task Group 2) - Update all Qute templates to use new CSS classes
3. Evaluation Git Tracking (Task Group 3) - Ensure evaluation files are included in the build
4. Visual Verification (Task Group 4) - Review all changes and run final tests

## Files Modified

**CSS (1 file):**
- `thoughts-admin/src/main/resources/META-INF/resources/css/styles.css`

**Templates (6 files):**
- `thoughts-admin/src/main/resources/templates/DashboardResource/dashboard.html`
- `thoughts-admin/src/main/resources/templates/ThoughtResource/thoughts.html`
- `thoughts-admin/src/main/resources/templates/ThoughtResource/detail.html`
- `thoughts-admin/src/main/resources/templates/RatingsResource/ratings.html`
- `thoughts-admin/src/main/resources/templates/EvaluationResource/evaluations.html`
- `thoughts-admin/src/main/resources/templates/EvaluationResource/stats.html`

**Java (1 file - git tracking only, no content changes):**
- `thoughts-admin/src/main/java/com/redhat/demos/thoughts/admin/resource/EvaluationResource.java`

**Templates NOT modified (confirmed already matching):**
- `layout.html` - navbar, footer, head section already match
- `create.html` - form structure already matches
- `edit.html` - form structure already matches
