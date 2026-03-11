# Specification: Admin Site Styling

## Goal
Apply Red Hat branding, custom typography, and visual polish to the Quarkus admin site through a custom CSS file and template updates, without modifying any backend Java code or changing page layouts.

## User Stories
- As a demo runner, I want the admin site to reflect Red Hat branding so that it looks professional and on-brand during customer demonstrations
- As a workshop attendee, I want a visually polished admin interface so that the demo application feels like a real enterprise tool rather than a plain Bootstrap scaffold

## Specific Requirements

**Custom CSS File Creation**
- Create `src/main/resources/META-INF/resources/css/styles.css` as the single custom stylesheet for all branding overrides
- Define CSS custom properties (variables) for the Red Hat brand palette: Red Hat Red (#EE0000), black (#151515), white (#FFFFFF), and supporting grays (#6A6E73, #D2D2D2, #F0F0F0)
- Load this stylesheet in `layout.html` after the Bootstrap 5 CDN link so custom rules properly override Bootstrap defaults
- Keep custom CSS minimal and targeted; use Bootstrap utility classes wherever possible and only write overrides where Bootstrap alone cannot achieve the desired result

**Google Fonts Integration**
- Add Google Fonts `<link>` tags to the `<head>` in `layout.html` for Red Hat Display (weights 400, 700) and Red Hat Text (weights 400, 500, 700)
- Apply Red Hat Display as the font family for all headings (h1 through h6) via CSS
- Apply Red Hat Text as the font family for body text, form controls, nav links, and all other non-heading elements
- Set appropriate font weights: 700 for headings, 400 for body copy, 500 for labels and emphasis

**Bootstrap Icons CDN Integration**
- Add the Bootstrap Icons CDN stylesheet link to `layout.html` in the `<head>` section
- Icons will be used in dashboard stat cards and optionally in the footer

**Navbar Branding**
- Override the navbar background to use Red Hat Red (#EE0000) instead of Bootstrap's generic `bg-dark`
- Keep the navbar text white for contrast against the red background
- Style the navbar brand text with Red Hat Display font at a slightly larger weight

**Dashboard Stat Card Enhancements**
- Replace the flat Bootstrap color classes (`bg-primary`, `bg-success`, `bg-danger`) on the top-row stat cards with subtle CSS gradients using Red Hat brand colors
- Add `box-shadow` for depth and a slight border-radius increase for a modern card feel
- Add Bootstrap Icons to each stat card (e.g., `bi-lightbulb` for Total Thoughts, `bi-hand-thumbs-up` for Thumbs Up, `bi-hand-thumbs-down` for Thumbs Down)
- Apply the same gradient and shadow treatment to the status count cards (Approved, Rejected, In Review) in the second dashboard row
- Apply the same gradient, shadow, and icon treatment to the stat cards on the Evaluation Statistics page

**Table Styling**
- Keep the existing `table-dark` header class on all tables across all pages; do not soften or lighten table headers
- Apply Red Hat Text font to table body content for improved readability
- Ensure table link colors use Red Hat Red (#EE0000) with appropriate hover state

**Form Styling**
- Apply Red Hat Text font to form labels, inputs, textareas, and help text on the Create and Edit Thought pages
- Override the Bootstrap primary button color to use Red Hat Red (#EE0000) as the background with white text
- Style form focus states (input borders, box shadows) to use a Red Hat Red tint instead of Bootstrap's default blue

**Badge and Button Color Overrides**
- Override Bootstrap's `btn-primary` and `bg-primary` to use Red Hat Red (#EE0000)
- Keep `bg-success`, `bg-danger`, and `bg-warning` badge colors as-is for semantic status indicators (APPROVED, REJECTED, IN_REVIEW)
- Override `btn-outline-primary` border and text color to match Red Hat Red

**Branded Footer**
- Add a `<footer>` element to `layout.html` below the `{#insert content}` container block so it appears on every page
- Include branding text such as "Positive Thoughts Admin -- A Red Hat Demo Application"
- Style with a dark background (#151515), white text, Red Hat Text font, and appropriate padding
- Keep the footer simple: one line of branding text, no links or complex layout

**Typography and Spacing Polish**
- Increase default body line-height slightly (e.g., 1.6) for improved readability with Red Hat Text
- Ensure consistent vertical spacing between page headings and content across all templates
- Apply letter-spacing adjustments to headings for a cleaner look with Red Hat Display
- Ensure `.lead` class text (used on the thought detail page) renders well with Red Hat Text

## Visual Design
No visual assets provided.

## Existing Code to Leverage

**layout.html (base template)**
- Currently loads Bootstrap 5 CSS and JS via CDN; the custom CSS link and Google Fonts links must be added to the existing `<head>` block after the Bootstrap CSS link
- The navbar markup already uses `navbar-dark bg-dark`; the `bg-dark` class should be overridden via CSS rather than changing the HTML class to keep changes minimal
- The footer must be added as a new element between the closing `</div>` of the container and the `<script>` tag
- The `{#insert content}` and `{#insert title}` blocks must remain unchanged

**dashboard.html (stat cards pattern)**
- Contains six stat cards in two rows using Bootstrap grid (`col-md-4`) and card components
- Top row cards use `text-white bg-primary`, `text-white bg-success`, `text-white bg-danger` -- these classes will be augmented with custom CSS gradient overrides
- Bottom row cards use `border-success`, `border-danger`, `border-warning` with centered text -- these need gradient backgrounds added via CSS classes
- The `display-4` and `display-6` classes on stat numbers should render well with Red Hat Display font

**stats.html (Evaluation Statistics cards)**
- Uses the same stat card pattern as the dashboard with `bg-primary`, `bg-success`, `bg-danger`, `bg-info` color classes
- Cards include percentage sub-text that should be legible against gradient backgrounds
- The summary card at the bottom uses default card styling and should inherit the Red Hat Text font

**All page templates (consistent patterns)**
- All nine templates use `{#include layout}` ensuring footer and font changes propagate automatically
- Tables consistently use `table-dark` header class which should be preserved
- Status badges use the same `bg-success`/`bg-danger`/`bg-warning` pattern across all pages

## Out of Scope
- Backend Java code changes (no resource classes, entities, or service modifications)
- Page layout restructuring or component reorganization
- Navigation changes (no breadcrumbs, sub-navigation, or dropdown menus)
- New pages or routes
- JavaScript functionality changes or additions
- Responsive breakpoint changes or mobile-specific redesign
- Replacing Bootstrap with another CSS framework
- Bootstrap Sass compilation or Bootswatch themes
- Authentication, authorization, or any non-visual features
- PatternFly CSS framework usage
