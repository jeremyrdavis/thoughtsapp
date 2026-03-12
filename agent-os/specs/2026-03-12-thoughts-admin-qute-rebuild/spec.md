# Specification: Thoughts Admin Qute Template Rebuild

## Goal
Rebuild all Qute templates and custom CSS in the thoughts-admin Quarkus app so they visually match the React-based thoughts-admin-frontend, achieving the same design language, layout, and brand appearance using Bootstrap 5 instead of Tailwind CSS.

## User Stories
- As an admin, I want the Qute-rendered admin site to look and feel identical to the React frontend so that both implementations present a consistent brand experience.
- As a developer, I want the Qute templates to follow the same visual patterns as the React frontend so that either implementation can serve as the admin UI without a jarring visual difference.

## Specific Requirements

**Table link color change**
- Change `.data-table a` color from `var(--rh-red)` to `var(--rh-black)` (`#151515`) in `styles.css`
- Keep `text-decoration: none` as default, `text-decoration: underline` on hover (already correct)
- This applies globally to all tables: dashboard Recent Activity, thoughts list, and ratings
- The React frontend uses `className="text-foreground hover:underline"` where `--foreground` is near-black

**Thumbs up/down text weight normalization**
- The current CSS defines `.text-success-bold` and `.text-destructive-bold` with `font-weight: 600`
- The React frontend uses plain `text-success` and `text-destructive` without extra font-weight in table cells
- Replace these classes with `.text-success { color: var(--success); }` and `.text-destructive { color: var(--destructive); }` (no font-weight)
- Update template references in `thoughts.html` and `ratings.html` from `text-success-bold` / `text-destructive-bold` to `text-success` / `text-destructive`
- On the detail page (`detail.html`), the React uses `text-xl font-bold`, so keep `fw-bold` as a separate Bootstrap class alongside `fs-4` and the new color class

**Sort button shape change on Ratings page**
- Remove `rounded-pill` class from the three sort `<a>` tags in `ratings.html`
- Keep them as `btn btn-sm btn-primary` / `btn btn-sm btn-outline-primary` (rectangular, Bootstrap default border-radius)
- Remove the `.sort-group .btn.rounded-pill` CSS rule from `styles.css`
- Tighten the button gap from `gap-2` to `gap-1` to match the React `flex gap-1` pattern

**Container max-width customization**
- Add a CSS rule: `@media (min-width: 1200px) { .container { max-width: 1400px; } }` to `styles.css`
- The current `.content-area` has `padding: 2rem 0`; this is sufficient since the Bootstrap `.container` already provides horizontal padding
- The React frontend uses `max-width: 1400px` with `padding: 2rem` on its container class

**Evaluations templates inclusion**
- The `evaluations.html` and `stats.html` templates under `EvaluationResource/` already exist on disk with correct content
- The `EvaluationResource.java` already exists with `@CheckedTemplate` bindings for both pages
- These files show as deleted in git status and must be staged/tracked so they are included in the build
- No structural content changes needed; only the button styling tweaks below

**Evaluation button styling adjustments**
- In `evaluations.html`, the "View Statistics" button currently uses `btn-outline-primary btn-sm`
- The React screenshot shows a gray-bordered outline button at default size (not small)
- Change to `btn-outline-secondary` and remove `btn-sm` to match
- In `stats.html`, the "Back to Evaluations" button already uses `btn-outline-secondary btn-sm`; remove `btn-sm` to use default sizing

**Dashboard heading spacing**
- The current Qute template uses `<h2 class="mb-3">Recent Activity</h2>`
- The React version uses `mb-4` spacing below this heading
- Change to `<h2 class="mb-4">Recent Activity</h2>`

**Thought detail thumbs up/down styling update**
- The current Qute template uses `text-success-bold fs-4` and `text-destructive-bold fs-4`
- After the class rename, use `text-success fw-bold fs-4` and `text-destructive fw-bold fs-4`
- This matches the React `text-xl font-bold text-success` / `text-destructive` pattern

**CSS cleanup**
- Remove the `.text-success-bold` and `.text-destructive-bold` class definitions from `styles.css`
- Remove the `.sort-group .btn.rounded-pill` rule from `styles.css`
- Add `.text-success { color: var(--success); }` and `.text-destructive { color: var(--destructive); }` utility classes
- All other existing CSS (stat cards, status cards, status badges, navbar, footer, detail card, form card, info banner, pagination, form focus) already matches and requires no changes

**No changes needed to these templates**
- `layout.html` - navbar, footer, head section all already match the React Layout component
- `create.html` - form wrapper, form card, labels, inputs, buttons all already match React ThoughtCreate
- `edit.html` - identical structure to create, already matches React ThoughtEdit
- `dashboard.html` - stat cards, status cards structure already match; only the mb-3 to mb-4 tweak on Recent Activity heading
- `detail.html` - detail card layout, button placement, info banner all match; only the thumbs class name update

## Visual Design

**`planning/visuals/dashboard.png`**
- Red navbar (#EE0000) with "Thoughts Admin" brand left-aligned, four nav items with icons to the right
- Active nav item ("Dashboard") has semi-transparent white background highlight (rgba 255,255,255,0.2)
- Three gradient stat cards in a row: red (Total Thoughts), green (Total Thumbs Up), red-orange (Total Thumbs Down) with icon + label + large number
- Three bordered status cards below: Approved (green border, green-tinted bg), Rejected (red border, red-tinted bg), In Review (yellow border, yellow-tinted bg)
- "Recent Activity" table with dark (#212121) header, content links, author, status badges, muted date column
- Dark footer (#151515) with centered white text at 80% opacity

**`planning/visuals/thoughts.png`**
- "Thoughts" heading with "Create New Thought" red button (with plus icon) aligned right
- Full-width table with dark header row, 6 columns: Content, Author, Status, thumbs-up emoji, thumbs-down emoji, Created
- Status column shows pill-shaped badges (green Approved, red Rejected, yellow In Review)
- Thumbs up values in green, thumbs down in red, without bold weight
- Pagination controls centered below table with Previous/Page N/Next

**`planning/visuals/ratings.png`**
- Three RECTANGULAR sort buttons (not rounded-pill): "Most Rated" filled red, "Most Liked" and "Most Disliked" outlined
- Small gap between buttons matching `gap-1`
- Table with 4 columns: Content, thumbs-up emoji, thumbs-down emoji, Net Score
- Net Score column in bold weight

**`planning/visuals/evaluations.png`**
- "AI Evaluations" heading with "View Statistics" outline button (gray/secondary border, default size) aligned right
- Light red/pink info banner with red info-circle icon and placeholder text
- The button uses a bar-chart icon

**`planning/visuals/thought_detail.png`**
- "Back to Thoughts" small outline button above the detail card
- Detail card with bordered header: "Thought Detail" title, Edit (outline-warning with pencil icon) and Delete (red/danger with trash icon) buttons
- Content text in larger font (fs-5) with relaxed line-height
- 3-column grid: Author, Author Bio, Status (with pill badge)
- 4-column grid: Thumbs Up (green, bold, fs-4), Thumbs Down (red, bold, fs-4), Created, Updated
- "AI Evaluations" heading below with info banner stating no evaluations available

## Existing Code to Leverage

**Current styles.css custom properties and component classes**
- The `:root` variables (`--rh-red`, `--rh-black`, `--success`, `--destructive`, `--warning`) already match the React design tokens exactly
- Stat card, status card, status badge, detail card, form card, info banner, and pagination CSS classes are already well-structured and closely match the React equivalent
- Only targeted changes needed: table link color, thumbs classes, sort button pill removal, container width

**Current Qute template structure and data binding**
- All templates use `{#include layout}` inheritance which must be preserved
- Template data binding (`{thought.content}`, `{#for thought in thoughts}`, conditional `{#if}` blocks) is correct and must remain unchanged
- The `{#insert}` blocks for active nav state work correctly and match the React `active` class logic
- Form templates use proper Bootstrap validation classes (`is-invalid`, `invalid-feedback`) tied to server-side error maps

**EvaluationResource.java and templates**
- The Java resource class already has `@CheckedTemplate` with `evaluations()` and `stats()` methods
- Both template files exist on disk with content that matches the React Evaluations and EvaluationStats pages
- Only button styling tweaks needed (variant and size class changes)

**Bootstrap 5 and Bootstrap Icons CDNs**
- Already loaded in `layout.html`; no version or CDN changes needed
- Icon mappings already correct: `bi-speedometer2`, `bi-chat-quote`, `bi-bar-chart`, `bi-cpu`, `bi-lightbulb`, `bi-hand-thumbs-up/down`, `bi-check-circle`, `bi-x-circle`, `bi-hourglass-split`, `bi-arrow-left`, `bi-pencil`, `bi-trash`, `bi-info-circle`, `bi-plus-lg`

**Red Hat Display and Red Hat Text Google Fonts**
- Already loaded via Google Fonts CDN in `layout.html`
- Typography rules in styles.css already match (700 weight headings, -0.02em letter-spacing, Red Hat Text body)

## Out of Scope
- Any changes to Java resource classes, model records, REST client interfaces, or backend logic
- Client-side JavaScript features (live character counters, toast notifications, React Query data fetching)
- AlertDialog modal for delete confirmation (keep simple `confirm()` approach)
- Pixel-perfect rendering match between Bootstrap 5 and Tailwind CSS
- Dark mode support
- Any new pages or features not present in the React frontend
- Responsive breakpoint behavior differences between Bootstrap and Tailwind grid systems
- Changes to the ThoughtApiResource JSON API endpoints
- Changes to Bootstrap or Bootstrap Icons CDN versions
- Adding new navigation items or routes beyond the existing four (Dashboard, Thoughts, Ratings, Evaluations)
