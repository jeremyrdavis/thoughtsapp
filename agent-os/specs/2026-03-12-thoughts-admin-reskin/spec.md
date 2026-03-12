# Specification: Thoughts Admin Reskin

## Goal
Update all Qute templates in the thoughts-admin application to match the high-fidelity mockups by leveraging existing CSS classes from `styles.css`, adding responsive navbar behavior, active nav link state, and correcting visual discrepancies between the current templates and the design.

## User Stories
- As an admin, I want the admin dashboard to match the polished Red Hat branded design so that the application looks professional and consistent across all pages.
- As an admin using a mobile device, I want the navbar to collapse into a hamburger menu so that I can navigate the app on smaller screens.

## Specific Requirements

**Responsive navbar with collapse toggler in layout.html**
- Add `navbar-expand-lg` class to the `<nav>` element so the navbar collapses on mobile breakpoints
- Add a Bootstrap `navbar-toggler` button with a hamburger icon targeting a collapsible wrapper
- Wrap the `<ul class="navbar-nav">` inside a `<div class="collapse navbar-collapse">` with a matching ID
- The Bootstrap JS bundle is already included in layout.html, so no additional scripts are needed
- On desktop the nav links remain in a horizontal row as they currently appear

**Active nav link state per page**
- Use Qute `{#insert}` / `{#insert activeNav}` block pattern in layout.html to receive the active page identifier from each child template
- Each child template should declare the active page via something like `{#activeNav}dashboard{/activeNav}` (or `thoughts`, `ratings`, `evaluations`)
- In layout.html, apply the `active` CSS class to the matching nav link using a conditional check against the inserted value
- No Java/backend code changes are needed; this is handled entirely through Qute template sections

**Update table content links from black to red**
- In `styles.css`, change `.data-table a` color from `var(--rh-black)` to `var(--rh-red)` to match the mockup where all table content links appear in red/coral
- This is the only CSS change required; all other styling already exists in `styles.css`

**Add AI Evaluations section to thought detail page**
- Below the existing `detail-card` in `detail.html`, add an `<h2>` heading "AI Evaluations"
- Below the heading, add an `info-banner` div containing an `info-banner-icon` (using `bi-info-circle`) and the text "No evaluations available for this thought."
- This is a static placeholder section; no backend data is required

**Change ratings sort buttons from connected btn-group to individual pill buttons**
- Remove the wrapping `<div class="btn-group sort-group">` from `ratings.html`
- Replace with a `<div class="sort-group d-flex gap-2">` to space out individual buttons
- Add `rounded-pill` class to each sort button so they render as individual pill-shaped buttons rather than a connected group
- Active button uses `btn-primary`, inactive buttons use `btn-outline-primary` (this logic already exists)

**Restore evaluations.html template**
- This template was deleted (per git status) but must be recreated for the reskin
- Page title "AI Evaluations" with a "View Statistics" outline button (`btn-outline-primary btn-sm`) aligned right, using `bi-bar-chart` icon
- Below the title row, render an `info-banner` with `bi-info-circle` icon and text "Evaluations will be available once the AI evaluation service is connected."
- Must declare `{#activeNav}evaluations{/activeNav}` for active nav state

**Restore stats.html template**
- This template was also deleted and must be recreated
- Should include a "Back to Evaluations" link and a similar `info-banner` placeholder
- Must declare `{#activeNav}evaluations{/activeNav}` for active nav state

**Verify dashboard.html matches mockup**
- The current template already uses the correct CSS classes (`stat-card`, `status-card`, `data-table-wrap`, `data-table`, `status-badge`)
- Confirm stat card icons match the mockup: lightbulb for Total Thoughts, thumbs-up for Total Thumbs Up, thumbs-down for Total Thumbs Down
- Confirm status card icons: check-circle for Approved, x-circle for Rejected, hourglass-split for In Review
- Must declare `{#activeNav}dashboard{/activeNav}` for active nav state

**Verify thoughts list, create, and edit templates**
- `thoughts.html`: Already correctly structured; needs `{#activeNav}thoughts{/activeNav}` added
- `create.html`: Already uses `form-wrapper` and `form-card` correctly; needs `{#activeNav}thoughts{/activeNav}` added
- `edit.html`: Already uses `form-wrapper` and `form-card` correctly; needs `{#activeNav}thoughts{/activeNav}` added
- `detail.html`: Needs `{#activeNav}thoughts{/activeNav}` added in addition to the AI Evaluations section

**Add sort-group pill button CSS**
- Add a small CSS rule for `.sort-group .btn.rounded-pill` or equivalent to ensure proper spacing and pill shape when buttons are not inside a `btn-group`
- Keep the existing `.sort-group .btn` font-size rule

## Visual Design

**`planning/visuals/dashboard.png`**
- Red navbar with "Thoughts Admin" brand on left, nav links (Dashboard, Thoughts, Ratings, Evaluations) with Bootstrap Icons, Dashboard link has active pill highlight
- Three stat cards in a row: Total Thoughts (red gradient), Total Thumbs Up (green gradient), Total Thumbs Down (red-dark gradient) with icons and large bold numbers
- Three status cards below: Approved (green border), Rejected (red border), In Review (yellow border) with count values
- Recent Activity section with dark-header data table, content links in red, status badges as colored pills, date column
- Dark footer with centered "Positive Thoughts Admin -- A Red Hat Demo Application"

**`planning/visuals/thoughts.png`**
- "Thoughts" nav link active in navbar
- Page heading "Thoughts" left-aligned with "Create New Thought" red button (with plus icon) right-aligned
- Full-width data table with columns: Content (red links), Author, Status (pill badges), thumbs-up icon, thumbs-down icon, Created
- Pagination controls at bottom

**`planning/visuals/ratings.png`**
- "Ratings" nav link active in navbar
- Title "Ratings Overview" followed by sort buttons styled as individual separated rounded pill buttons (not a connected btn-group)
- Active sort button filled red, inactive ones outlined red
- Data table with columns: Content (red links), thumbs-up icon, thumbs-down icon, Net Score

**`planning/visuals/evaluations.png`**
- "Evaluations" nav link active in navbar
- Title "AI Evaluations" with "View Statistics" outline button right-aligned (bar-chart icon)
- Info-banner below with red info-circle icon and message about AI evaluation service not connected
- Light red/pink background on the info-banner with subtle red border

**`planning/visuals/thought_detail.png`**
- "Thoughts" nav link active in navbar
- "Back to Thoughts" link with left arrow above the detail card
- Detail card with header containing "Thought Detail" title, Edit (outline warning) and Delete (red filled) buttons
- Detail body: full content text, then metadata row (Author, Author Bio, Status badge), then second row (Thumbs Up green, Thumbs Down red, Created datetime, Updated datetime)
- Below detail card: "AI Evaluations" heading with info-banner stating "No evaluations available for this thought."

## Existing Code to Leverage

**styles.css component classes**
- All CSS component classes needed for the reskin already exist: `stat-card`, `status-card`, `data-table-wrap`, `data-table`, `detail-card`, `form-card`, `form-wrapper`, `status-badge`, `info-banner`, `sort-group`, `pagination-simple`
- The only CSS change needed is updating `.data-table a` color from `var(--rh-black)` to `var(--rh-red)`
- Minor addition for sort-group pill button spacing when not in a `btn-group`

**layout.html Qute template inheritance**
- All child templates already use `{#include layout}` with `{#title}` and `{#content}` insert blocks
- The `{#insert}`/`{#insert}` pattern should be extended to add an `{#activeNav}` block for passing the active page identifier
- Bootstrap JS bundle is already loaded, enabling the navbar toggler without additional scripts

**Existing template structure and data bindings**
- All Qute expression bindings (e.g., `{thought.truncatedContent(80)}`, `{thought.status.name()}`, `{thought.thumbsUp}`) are already correct and working
- No template data bindings need to change; this is purely HTML/CSS restructuring
- The conditional status badge rendering pattern (`{#if thought.status.name() == 'APPROVED'}`) is consistent across all templates and should be preserved

**Bootstrap 5 responsive navbar pattern**
- Bootstrap 5 CDN is already included (CSS and JS bundle) in layout.html
- Standard Bootstrap navbar-expand-lg pattern with navbar-toggler and collapse can be applied directly
- The existing custom navbar CSS (`navbar`, `navbar-brand`, `nav-link`, `nav-link.active`) is compatible with Bootstrap's responsive collapse structure

**Ratings sort button existing logic**
- The conditional active/inactive button class logic already works correctly in ratings.html: `{#if currentSort == 'most-rated'}btn-primary{#else}btn-outline-primary{/if}`
- Only the wrapper markup needs to change from `btn-group` to a flex container with gap, and `rounded-pill` added to each button

## Out of Scope
- Backend Java code changes (resources, models, REST clients, filters)
- Adding new routes, pages, or endpoints
- Changing data/item counts, pagination behavior, or query logic
- JavaScript functionality beyond Bootstrap's built-in navbar toggler
- Database, API, or Kafka changes
- Adding actual AI evaluation data or integration (the evaluations section is a static placeholder)
- Changing the existing Bootstrap or Google Fonts CDN versions
- Adding new CSS custom properties or design tokens beyond the minor link color and pill button adjustments
- Modifying form validation logic or error handling behavior
- Performance optimization or CSS purging
