# Task Breakdown: Admin Site Styling

## Overview
Total Tasks: 22

This feature is purely CSS and HTML template work -- no backend Java code, no new pages, no layout restructuring. The work is organized into four groups that build on each other: foundational setup in the layout template, the custom CSS file with all brand overrides, template-level markup additions for icons, and a final visual verification pass.

## Task List

### Foundation Layer

#### Task Group 1: Layout Template Setup (CDN Links, Footer, CSS File Scaffold)
**Dependencies:** None

- [x] 1.0 Complete layout template foundation
  - [x] 1.1 Write 3 focused tests for layout template changes
    - Test that `layout.html` includes a Google Fonts link tag for Red Hat Display and Red Hat Text
    - Test that `layout.html` includes a Bootstrap Icons CDN link tag
    - Test that `layout.html` includes a link to `/css/styles.css` after the Bootstrap CSS link
  - [x] 1.2 Add Google Fonts link tags to `layout.html`
    - Add `<link>` tags in the `<head>` section after the Bootstrap CSS link for Red Hat Display (weights 400, 700) and Red Hat Text (weights 400, 500, 700)
    - Use the standard Google Fonts preconnect and stylesheet pattern
  - [x] 1.3 Add Bootstrap Icons CDN link to `layout.html`
    - Add the Bootstrap Icons CDN stylesheet link in the `<head>` section after Google Fonts links
  - [x] 1.4 Create the custom CSS file scaffold
    - Create `src/main/resources/META-INF/resources/css/styles.css` with CSS custom properties block defining the Red Hat brand palette
    - Variables to define: `--rh-red: #EE0000`, `--rh-black: #151515`, `--rh-white: #FFFFFF`, `--rh-gray-dark: #6A6E73`, `--rh-gray-mid: #D2D2D2`, `--rh-gray-light: #F0F0F0`
  - [x] 1.5 Add the custom stylesheet link to `layout.html`
    - Add `<link rel="stylesheet" href="/css/styles.css">` in the `<head>` after all CDN links so custom rules override Bootstrap defaults
  - [x] 1.6 Add branded footer to `layout.html`
    - Insert a `<footer>` element between the closing `</div>` of the container and the `<script>` tag
    - Include text: "Positive Thoughts Admin -- A Red Hat Demo Application"
    - The footer will be styled in Task Group 2; use minimal inline structure for now (just the element and text)
  - [x] 1.7 Ensure layout template tests pass
    - Run ONLY the 3 tests written in 1.1
    - Verify the template renders with all new link tags and the footer element

**Acceptance Criteria:**
- The 3 tests written in 1.1 pass
- `layout.html` loads Google Fonts (Red Hat Display + Red Hat Text), Bootstrap Icons CDN, and `/css/styles.css` in the correct order after Bootstrap CSS
- `styles.css` exists at the correct Quarkus static resource path with CSS custom properties defined
- A footer element with branding text is present in the layout template
- All existing `{#insert}` blocks remain unchanged

### Styling Layer

#### Task Group 2: Custom CSS -- Typography, Navbar, Footer, and Global Overrides
**Dependencies:** Task Group 1

- [x] 2.0 Complete global CSS overrides
  - [x] 2.1 Write 4 focused tests for CSS output
    - Test that the navbar background renders as Red Hat Red (#EE0000) rather than Bootstrap default dark
    - Test that heading elements use `Red Hat Display` font family
    - Test that body text uses `Red Hat Text` font family
    - Test that the footer has dark background (#151515) and white text
  - [x] 2.2 Add typography rules to `styles.css`
    - Set `body` font-family to `'Red Hat Text', sans-serif` with `line-height: 1.6`
    - Set `h1, h2, h3, h4, h5, h6` font-family to `'Red Hat Display', sans-serif` with `font-weight: 700` and `letter-spacing: -0.02em`
    - Set `form-control`, `form-label`, `nav-link`, and `.form-text` to inherit `'Red Hat Text'`
    - Set `font-weight: 500` on `.form-label` elements
    - Ensure `.lead` class text renders well with Red Hat Text
  - [x] 2.3 Add navbar branding overrides to `styles.css`
    - Override `.navbar.bg-dark` background-color to `var(--rh-red)`
    - Keep navbar text white for contrast
    - Style `.navbar-brand` with `'Red Hat Display'` font and `font-weight: 700`
  - [x] 2.4 Add footer styles to `styles.css`
    - Style `footer` with `background-color: var(--rh-black)`, white text, `'Red Hat Text'` font
    - Add appropriate padding (`1rem 0` or similar) and center the text
    - Keep it simple: single line of text appearance
  - [x] 2.5 Add button and badge color overrides to `styles.css`
    - Override `.btn-primary` background to `var(--rh-red)` with `border-color: var(--rh-red)` and white text
    - Override `.btn-primary:hover` with a slightly darker red
    - Override `.btn-outline-primary` border and text color to `var(--rh-red)`
    - Override `.bg-primary` background to `var(--rh-red)`
    - Preserve `.bg-success`, `.bg-danger`, `.bg-warning` as-is for semantic badges
  - [x] 2.6 Add form focus state overrides to `styles.css`
    - Override `.form-control:focus` border-color and box-shadow to use a Red Hat Red tint instead of Bootstrap blue
  - [x] 2.7 Add table link color overrides to `styles.css`
    - Style table anchor tags to use `var(--rh-red)` with appropriate hover darkening
    - Ensure `table-dark` header class is not overridden (preserve existing high-contrast headers)
  - [x] 2.8 Ensure CSS override tests pass
    - Run ONLY the 4 tests written in 2.1
    - Verify navbar, typography, and footer styles render correctly

**Acceptance Criteria:**
- The 4 tests written in 2.1 pass
- All headings use Red Hat Display; all body text uses Red Hat Text
- Navbar is Red Hat Red with white text
- Footer has dark background with white branding text
- Primary buttons and badges use Red Hat Red
- Form focus states use Red Hat Red tint
- Table links use Red Hat Red; table-dark headers are untouched
- CSS is minimal and targeted, using CSS custom properties throughout

#### Task Group 3: Custom CSS -- Stat Card Enhancements (Gradients, Shadows, Icons)
**Dependencies:** Task Group 1

- [x] 3.0 Complete stat card styling
  - [x] 3.1 Write 3 focused tests for stat card enhancements
    - Test that dashboard stat cards have `box-shadow` applied
    - Test that stat cards have CSS gradient backgrounds instead of flat Bootstrap colors
    - Test that Bootstrap Icon `<i>` elements are present in dashboard stat cards
  - [x] 3.2 Add stat card gradient and shadow CSS to `styles.css`
    - Override `.card.bg-primary` with a subtle gradient using Red Hat Red shades (e.g., `linear-gradient(135deg, #EE0000, #A30000)`)
    - Override `.card.bg-success` with a gradient using green shades
    - Override `.card.bg-danger` with a gradient using red/dark-red shades
    - Override `.card.bg-info` with a gradient using teal/blue shades (for stats page)
    - Add `box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1)` to all stat cards
    - Increase `border-radius` slightly for a modern feel (e.g., `0.5rem`)
    - Add gradient and shadow treatment to bottom-row status cards (`.card.border-success`, `.card.border-danger`, `.card.border-warning`) with lighter gradient backgrounds
  - [x] 3.3 Add Bootstrap Icons to dashboard stat cards in `dashboard.html`
    - Add `<i class="bi bi-lightbulb"></i>` to the Total Thoughts card title
    - Add `<i class="bi bi-hand-thumbs-up"></i>` to the Total Thumbs Up card title
    - Add `<i class="bi bi-hand-thumbs-down"></i>` to the Total Thumbs Down card title
    - Add appropriate icons to the Approved, Rejected, and In Review cards in the second row (e.g., `bi-check-circle`, `bi-x-circle`, `bi-hourglass-split`)
  - [x] 3.4 Add Bootstrap Icons to stats page cards in `stats.html`
    - Add `<i class="bi bi-clipboard-data"></i>` to Total Evaluations card
    - Add `<i class="bi bi-check-circle"></i>` to Approved card
    - Add `<i class="bi bi-x-circle"></i>` to Rejected card
    - Add `<i class="bi bi-graph-up"></i>` or similar to Avg Similarity Score card
  - [x] 3.5 Add icon spacing CSS to `styles.css`
    - Add a small right margin to `.card-title i` or `.bi` elements inside card titles for consistent spacing between icon and text
  - [x] 3.6 Ensure stat card tests pass
    - Run ONLY the 3 tests written in 3.1
    - Verify gradients, shadows, and icons render on dashboard and stats pages

**Acceptance Criteria:**
- The 3 tests written in 3.1 pass
- Dashboard top-row cards show gradient backgrounds instead of flat Bootstrap colors
- Dashboard bottom-row status cards have gradient backgrounds and shadows
- Stats page cards have matching gradient and shadow treatment
- All stat cards display appropriate Bootstrap Icons next to their titles
- Cards have enhanced depth via box-shadow and slightly increased border-radius
- Percentage sub-text on stats cards remains legible against gradient backgrounds

### Verification Layer

#### Task Group 4: Visual Verification and Polish
**Dependencies:** Task Groups 1-3

- [x] 4.0 Review and verify all styling across every page
  - [x] 4.1 Review all tests from Task Groups 1-3
    - Review the 3 tests from Task Group 1 (layout template)
    - Review the 4 tests from Task Group 2 (CSS overrides)
    - Review the 3 tests from Task Group 3 (stat cards)
    - Total existing tests: 10
  - [x] 4.2 Identify critical visual gaps across all 9 templates
    - Check `thoughts.html`, `ratings.html`, `evaluations.html`, `edit.html` for any styling inconsistencies not covered by global CSS
    - Verify font inheritance works on all pages through the `{#include layout}` pattern
    - Verify badge colors remain correct for APPROVED/REJECTED/IN_REVIEW status across all pages
    - Confirm the footer appears on every page
  - [x] 4.3 Write up to 5 additional tests to cover cross-page consistency
    - Test that the footer renders on a non-dashboard page (e.g., thoughts list)
    - Test that form pages (create, edit) inherit Red Hat Text on form controls
    - Test that table links on list pages (thoughts, ratings, evaluations) use Red Hat Red
    - Test that status badges retain correct semantic colors across pages
    - Test that `.display-4` and `.display-6` stat numbers render with Red Hat Display font
  - [x] 4.4 Run all feature-specific tests
    - Run ONLY tests related to this feature (tests from 1.1, 2.1, 3.1, and 4.3)
    - Expected total: approximately 15 tests
    - Verify all pass

**Acceptance Criteria:**
- All 15 feature-specific tests pass
- Red Hat branding is consistent across all 9 page templates
- Footer appears on every page via layout inheritance
- No visual regressions in semantic badge colors or table header styling
- Typography hierarchy is consistent (Red Hat Display for headings, Red Hat Text for body)
- CSS file remains minimal and well-organized with custom properties

## Execution Order

Recommended implementation sequence:
1. Layout Template Setup (Task Group 1) -- establishes the foundation by adding all CDN links, the CSS file, and the footer element
2. Global CSS Overrides (Task Group 2) and Stat Card Enhancements (Task Group 3) -- these two groups can be implemented in parallel since both write to `styles.css` in non-overlapping sections and Task Group 3 also modifies `dashboard.html` and `stats.html`
3. Visual Verification and Polish (Task Group 4) -- final pass to verify consistency across all pages and fill any test gaps

## Files Modified

- `thoughts-admin/src/main/resources/templates/layout.html` -- Google Fonts links, Bootstrap Icons CDN link, custom CSS link, footer element
- `thoughts-admin/src/main/resources/META-INF/resources/css/styles.css` -- new file with all custom CSS
- `thoughts-admin/src/main/resources/templates/DashboardResource/dashboard.html` -- Bootstrap Icon markup in stat cards
- `thoughts-admin/src/main/resources/templates/EvaluationResource/stats.html` -- Bootstrap Icon markup in stat cards

## Files Not Modified

- No backend Java files (resource classes, entities, services)
- No other template files (styling propagates via CSS and layout inheritance)
- No JavaScript files
