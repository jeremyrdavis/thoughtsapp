# Task Breakdown: Thoughts Admin Reskin

## Overview
Total Tasks: 21

This is a purely cosmetic reskin -- all changes are to Qute templates (HTML) and one CSS file. No backend Java code changes are required. The existing CSS in `styles.css` already defines nearly all needed component classes. The work involves updating templates to leverage those classes correctly, adding responsive navbar behavior, active nav link state, and correcting visual discrepancies against the high-fidelity mockups.

## Task List

### CSS Updates

#### Task Group 1: CSS Adjustments
**Dependencies:** None

- [x] 1.0 Complete CSS adjustments
  - [x] 1.1 Write 2 focused tests for CSS changes
    - Test that `.data-table a` color resolves to `var(--rh-red)` (not `var(--rh-black)`)
    - Test that `.sort-group .btn.rounded-pill` renders with pill border-radius
  - [x] 1.2 Update `.data-table a` color in `styles.css`
    - File: `thoughts-admin/src/main/resources/META-INF/resources/css/styles.css`
    - Change line 249: `color: var(--rh-black);` to `color: var(--rh-red);`
    - This makes all table content links appear in red/coral to match the mockups
    - Reference mockup: `planning/visuals/dashboard.png`, `planning/visuals/thoughts.png`, `planning/visuals/ratings.png`
  - [x] 1.3 Add sort-group pill button CSS rule
    - File: `thoughts-admin/src/main/resources/META-INF/resources/css/styles.css`
    - Add a rule for `.sort-group .btn.rounded-pill` to ensure proper spacing and pill shape when buttons are not inside a `btn-group`
    - Keep the existing `.sort-group .btn` font-size rule unchanged
    - Reference mockup: `planning/visuals/ratings.png` (individual separated pill buttons, not connected btn-group)
  - [x] 1.4 Ensure CSS tests pass
    - Run ONLY the 2 tests written in 1.1
    - Verify the CSS file compiles and loads without errors

**Acceptance Criteria:**
- Table content links render in red (`var(--rh-red)`) across all pages
- Sort buttons on the ratings page render as individual pill-shaped buttons
- No existing CSS rules are broken
- Only 2 lines changed plus 1 small rule added in `styles.css`

### Layout & Navigation

#### Task Group 2: Layout Template and Responsive Navbar
**Dependencies:** None (can run in parallel with Task Group 1)

- [x] 2.0 Complete layout template updates
  - [x] 2.1 Write 3 focused tests for layout and navbar behavior
    - Test that the navbar contains a `navbar-toggler` button element
    - Test that nav links are wrapped in a `collapse navbar-collapse` div
    - Test that the active nav link receives the `active` CSS class based on the `{#insert activeNav}` block value
  - [x] 2.2 Add responsive navbar toggler to `layout.html`
    - File: `thoughts-admin/src/main/resources/templates/layout.html`
    - Add `navbar-expand-lg` class to the `<nav>` element
    - Add a `<button class="navbar-toggler">` with hamburger icon (`navbar-toggler-icon`) targeting `id="navbarNav"`
    - Wrap the `<ul class="navbar-nav">` inside `<div class="collapse navbar-collapse" id="navbarNav">`
    - Bootstrap JS bundle is already included -- no additional scripts needed
    - On desktop, nav links remain in a horizontal row as they currently appear
    - Reference mockup: `planning/visuals/dashboard.png` (desktop navbar layout)
  - [x] 2.3 Add active nav link state mechanism to `layout.html`
    - Add `{#insert activeNav}{/insert}` block in `layout.html` to receive the active page identifier from child templates
    - Use a Qute `{#let}` or inline conditional to capture the inserted value and apply the `active` class to the matching nav `<a>` element
    - Pattern: each child template declares `{#activeNav}dashboard{/activeNav}` (or `thoughts`, `ratings`, `evaluations`)
    - The `active` CSS class is already styled in `styles.css` (line 92-95: semi-transparent white pill background)
    - No Java/backend code changes needed -- this is handled entirely through Qute template sections
  - [x] 2.4 Ensure layout tests pass
    - Run ONLY the 3 tests written in 2.1
    - Verify navbar collapses on mobile viewport and expands on desktop
    - Verify active nav state renders correctly

**Acceptance Criteria:**
- Navbar collapses into hamburger menu on screens below `lg` breakpoint (992px)
- Navbar expands to horizontal links on desktop
- Active nav link is visually highlighted with semi-transparent white pill background
- No changes to footer or content area structure

### Page Templates -- Active Nav State & Visual Corrections

#### Task Group 3: Dashboard and Thoughts Templates
**Dependencies:** Task Group 2 (requires activeNav block in layout.html)

- [x] 3.0 Complete dashboard and thoughts template updates
  - [x] 3.1 Write 4 focused tests for template rendering
    - Test that `dashboard.html` renders with `{#activeNav}dashboard{/activeNav}` producing an active Dashboard nav link
    - Test that `thoughts.html` renders with active Thoughts nav link
    - Test that `detail.html` includes an "AI Evaluations" heading and info-banner section
    - Test that `create.html` and `edit.html` render with active Thoughts nav link
  - [x] 3.2 Add active nav declaration to `dashboard.html`
    - File: `thoughts-admin/src/main/resources/templates/DashboardResource/dashboard.html`
    - Add `{#activeNav}dashboard{/activeNav}` inside the `{#include layout}` block
    - Verify stat card icons match mockup: `bi-lightbulb` (Total Thoughts), `bi-hand-thumbs-up` (Total Thumbs Up), `bi-hand-thumbs-down` (Total Thumbs Down)
    - Verify status card icons: `bi-check-circle` (Approved), `bi-x-circle` (Rejected), `bi-hourglass-split` (In Review)
    - No other changes needed -- existing template already uses correct CSS classes
    - Reference mockup: `planning/visuals/dashboard.png`
  - [x] 3.3 Add active nav declaration to `thoughts.html`
    - File: `thoughts-admin/src/main/resources/templates/ThoughtResource/thoughts.html`
    - Add `{#activeNav}thoughts{/activeNav}` inside the `{#include layout}` block
    - Verify table structure, "Create New Thought" button placement, and pagination match mockup
    - No other changes needed
    - Reference mockup: `planning/visuals/thoughts.png`
  - [x] 3.4 Update `detail.html` with active nav and AI Evaluations section
    - File: `thoughts-admin/src/main/resources/templates/ThoughtResource/detail.html`
    - Add `{#activeNav}thoughts{/activeNav}` inside the `{#include layout}` block
    - Below the existing `detail-card mb-4` div, add an `<h2 class="mb-3">AI Evaluations</h2>` heading
    - Below the heading, add: `<div class="info-banner"><i class="bi bi-info-circle info-banner-icon"></i>No evaluations available for this thought.</div>`
    - This is a static placeholder -- no backend data binding required
    - Reference mockup: `planning/visuals/thought_detail.png`
  - [x] 3.5 Add active nav declaration to `create.html` and `edit.html`
    - File: `thoughts-admin/src/main/resources/templates/ThoughtResource/create.html`
    - File: `thoughts-admin/src/main/resources/templates/ThoughtResource/edit.html`
    - Add `{#activeNav}thoughts{/activeNav}` to both templates
    - No other changes needed -- both already use `form-wrapper` and `form-card` correctly
  - [x] 3.6 Ensure dashboard and thoughts template tests pass
    - Run ONLY the 4 tests written in 3.1
    - Verify active nav state renders on each page
    - Verify AI Evaluations section appears on thought detail page

**Acceptance Criteria:**
- Dashboard, thoughts list, detail, create, and edit pages all show the correct active nav link
- Thought detail page includes the "AI Evaluations" section with info-banner below the detail card
- All existing data bindings and Qute expressions remain unchanged
- Templates match their respective mockups

#### Task Group 4: Ratings and Evaluations Templates
**Dependencies:** Task Group 2 (requires activeNav block in layout.html)

- [x] 4.0 Complete ratings and evaluations template updates
  - [x] 4.1 Write 4 focused tests for template rendering
    - Test that `ratings.html` renders sort buttons as individual pill buttons (not a connected btn-group)
    - Test that `ratings.html` renders with active Ratings nav link
    - Test that `evaluations.html` renders with active Evaluations nav link and contains "View Statistics" button
    - Test that `stats.html` renders with active Evaluations nav link and contains "Back to Evaluations" link
  - [x] 4.2 Update sort buttons in `ratings.html`
    - File: `thoughts-admin/src/main/resources/templates/RatingsResource/ratings.html`
    - Add `{#activeNav}ratings{/activeNav}` inside the `{#include layout}` block
    - Remove the wrapping `<div class="btn-group sort-group">` (line 7)
    - Replace with `<div class="sort-group d-flex gap-2">` to space out individual buttons
    - Add `rounded-pill` class to each sort button (`<a>` elements)
    - Keep the existing active/inactive button class logic: `{#if currentSort == 'most-rated'}btn-primary{#else}btn-outline-primary{/if}`
    - Reference mockup: `planning/visuals/ratings.png`
  - [x] 4.3 Update `evaluations.html` with active nav and button styling
    - File: `thoughts-admin/src/main/resources/templates/EvaluationResource/evaluations.html`
    - Add `{#activeNav}evaluations{/activeNav}` inside the `{#include layout}` block
    - Change the "View Statistics" button class from `btn-outline-secondary` to `btn-outline-primary` to match mockup
    - Change the icon from `bi-bar-chart-line` to `bi-bar-chart` to match mockup
    - Verify info-banner text and structure match mockup
    - Reference mockup: `planning/visuals/evaluations.png`
  - [x] 4.4 Update `stats.html` with active nav
    - File: `thoughts-admin/src/main/resources/templates/EvaluationResource/stats.html`
    - Add `{#activeNav}evaluations{/activeNav}` inside the `{#include layout}` block
    - Verify info-banner and "Back to Evaluations" link are present and correctly structured
    - No other changes needed
  - [x] 4.5 Ensure ratings and evaluations template tests pass
    - Run ONLY the 4 tests written in 4.1
    - Verify sort buttons render as separated pills
    - Verify active nav state on ratings and evaluations pages

**Acceptance Criteria:**
- Ratings sort buttons render as individual separated pill buttons, not a connected btn-group
- Active sort button is filled red (`btn-primary`), inactive buttons are outline red (`btn-outline-primary`)
- Evaluations page shows "View Statistics" button with correct styling and icon
- Stats page shows "Back to Evaluations" link
- Both evaluations pages declare `evaluations` as active nav
- Ratings page declares `ratings` as active nav

### Visual Verification

#### Task Group 5: Cross-Page Visual Verification and Test Gap Analysis
**Dependencies:** Task Groups 1-4

- [x] 5.0 Review and verify all changes against mockups
  - [x] 5.1 Review tests from Task Groups 1-4
    - Review the 2 CSS tests from Task Group 1
    - Review the 3 layout/navbar tests from Task Group 2
    - Review the 4 dashboard/thoughts tests from Task Group 3
    - Review the 4 ratings/evaluations tests from Task Group 4
    - Total existing tests: approximately 13 tests
  - [x] 5.2 Analyze test coverage gaps for this feature only
    - Identify any critical visual workflows that lack test coverage
    - Focus ONLY on gaps related to this reskin's requirements
    - Prioritize responsive navbar behavior and active nav state consistency
  - [x] 5.3 Write up to 5 additional strategic tests to fill critical gaps
    - Test responsive navbar toggler visibility at mobile breakpoint
    - Test that all 9 templates compile without Qute errors
    - Test that the navbar contains exactly 4 nav links (Dashboard, Thoughts, Ratings, Evaluations)
    - Test that table links across dashboard, thoughts, and ratings pages are styled consistently
    - Test that the evaluations info-banner renders with correct icon and text
  - [x] 5.4 Run all feature-specific tests
    - Run ONLY the tests related to this reskin (tests from 1.1, 2.1, 3.1, 4.1, and 5.3)
    - Expected total: approximately 18 tests maximum
    - Do NOT run the entire application test suite
    - Verify all pages render correctly and match mockups

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 18 tests total)
- Every page's active nav link matches the expected page
- Table content links are red across all pages
- Ratings sort buttons are individual pills
- Thought detail page includes AI Evaluations placeholder section
- Responsive navbar collapses and expands correctly
- No Qute template compilation errors

## Execution Order

Recommended implementation sequence:

1. **CSS Updates (Task Group 1)** and **Layout & Navigation (Task Group 2)** -- these are independent and can be implemented in parallel
2. **Dashboard and Thoughts Templates (Task Group 3)** and **Ratings and Evaluations Templates (Task Group 4)** -- these depend on the activeNav block from Task Group 2 but are independent of each other, so they can be implemented in parallel
3. **Visual Verification (Task Group 5)** -- depends on all previous groups being complete

```
Task Group 1 (CSS) ──────────────────┐
                                      ├──> Task Group 3 (Dashboard/Thoughts) ──┐
Task Group 2 (Layout/Navbar) ────────┤                                          ├──> Task Group 5 (Verification)
                                      ├──> Task Group 4 (Ratings/Evaluations) ──┘
                                      │
                                      └──> (parallel with Group 1)
```

## Files Modified

| File | Task Group | Change |
|------|------------|--------|
| `styles.css` | 1 | Update `.data-table a` color, add pill button rule |
| `layout.html` | 2 | Add navbar-expand-lg, toggler, collapse wrapper, activeNav block |
| `dashboard.html` | 3 | Add `{#activeNav}dashboard{/activeNav}` |
| `thoughts.html` | 3 | Add `{#activeNav}thoughts{/activeNav}` |
| `detail.html` | 3 | Add `{#activeNav}thoughts{/activeNav}`, add AI Evaluations section |
| `create.html` | 3 | Add `{#activeNav}thoughts{/activeNav}` |
| `edit.html` | 3 | Add `{#activeNav}thoughts{/activeNav}` |
| `ratings.html` | 4 | Add `{#activeNav}ratings{/activeNav}`, change btn-group to flex pills |
| `evaluations.html` | 4 | Add `{#activeNav}evaluations{/activeNav}`, update button styling |
| `stats.html` | 4 | Add `{#activeNav}evaluations{/activeNav}` |

## Key Technical Notes

- **No Java changes**: All modifications are to `.html` Qute templates and `styles.css` only
- **Qute activeNav pattern**: Uses `{#insert activeNav}{/insert}` in layout.html with child templates providing the value via `{#activeNav}pageName{/activeNav}`. The layout then uses conditional logic to apply the `active` class to the matching nav link
- **Bootstrap navbar**: The `navbar-expand-lg` class combined with the existing Bootstrap JS bundle handles responsive collapse automatically
- **Evaluations templates**: Already exist on disk (not actually deleted from filesystem despite git status showing `D`), so they need updating rather than full recreation
- **Sort button change**: Only the wrapper markup changes from `btn-group` to `d-flex gap-2`, and `rounded-pill` is added to each button. The active/inactive conditional logic is preserved as-is
