# Spec Requirements: Thoughts Admin Reskin

## Initial Description
Re-skin the thoughts-admin Quarkus app to match design mockups. The CSS is already in place in `styles.css`. The work is purely cosmetic -- updating Qute templates to properly leverage existing CSS classes and match the visual design shown in mockup screenshots. No backend/Java changes required.

## Requirements Discussion

### First Round Questions

**Q1:** I assume the goal is purely visual -- updating the Qute template HTML to use the CSS classes already defined in `styles.css`, with no changes to backend Java code or data models. Is that correct, or are there functional changes involved as well?
**Answer:** Yes, purely cosmetic -- no backend/Java changes.

**Q2:** I'm assuming the current templates may be using default Bootstrap classes/structure that don't leverage the custom CSS classes already in `styles.css`. Should we treat the existing CSS as the "source of truth" for what components and class names to use, and update all templates to match?
**Answer:** Yes, treat the existing CSS as source of truth for class names and component patterns.

**Q3:** The layout.html currently has a simple navbar with inline nav links (no mobile hamburger toggle). I assume we should keep this desktop-focused layout since this is an admin tool. Should we add Bootstrap's responsive navbar toggler for mobile, or is desktop-only acceptable?
**Answer:** Yes, add Bootstrap's responsive navbar toggler for mobile.

**Q4:** The Evaluations pages (evaluations.html and stats.html) are listed in the git status as deleted files. Should these templates be included in the reskin, or are they being removed from the app entirely?
**Answer:** Yes, include the evaluations pages in the reskin.

**Q5:** I assume the nav link "active" state should be set correctly per page (e.g., Dashboard link highlighted when on /, Thoughts link highlighted when on /thoughts). Is that already handled in the templates, or should we add that as part of this work?
**Answer:** Yes, add active state handling for nav links per page.

**Q6:** For the dashboard's "Recent Activity" table and the Thoughts list table, I assume we should use the `data-table-wrap` and `data-table` CSS classes with the dark header style already defined. Is there any preference for how many items to show, or should we keep whatever the current templates have?
**Answer:** Current item counts are fine, keep them as-is.

**Q7:** Are there any pages or elements you specifically want to exclude from this reskin, or anything that should remain unchanged?
**Answer:** No exclusions -- reskin everything.

### Existing Code to Reference
No similar existing features identified for reference. The CSS file (`styles.css`) itself serves as the design system reference for all component patterns and class names.

### Follow-up Questions
No follow-up questions were needed. The user's answers were clear and comprehensive.

## Visual Assets

### Files Provided:
- `dashboard.png`: Dashboard page mockup showing Red Hat branded navbar (red background, white text, "Thoughts Admin" brand, nav links with icons for Dashboard/Thoughts/Ratings/Evaluations with active state pill highlight). Three stat cards in a row (Total Thoughts in red gradient, Total Thumbs Up in green gradient, Total Thumbs Down in red-orange gradient) with icons and large numbers. Three status cards below (Approved with green border, Rejected with red border, In Review with yellow border) showing counts with checkmark/x/hourglass icons. "Recent Activity" section with a data table (dark header, content links in red/coral color, Author column, Status badges as green pills, Updated dates). Footer in dark/black with "Positive Thoughts Admin -- A Red Hat Demo Application".

- `thoughts.png`: Thoughts list page showing the same navbar with "Thoughts" nav link active. Page heading "Thoughts" with a red "Create New Thought" button (with plus icon) on the right. Full-width data table with dark header row containing columns: Content, Author, Status, thumbs-up icon, thumbs-down icon, Created. Content column shows truncated text as links. Status column uses green "Approved" pill badges. Thumbs up/down values shown in green/red respectively. Pagination controls visible at bottom.

- `ratings.png`: Ratings Overview page with "Ratings" active in navbar. Title "Ratings Overview" followed by a sort button group with three options: "Most Rated" (filled/active in red), "Most Liked" (outline), "Most Disliked" (outline) -- styled as individual rounded pill buttons rather than a connected btn-group. Data table with dark header showing Content, thumbs-up icon, thumbs-down icon, Net Score columns. Content links in red/coral color.

- `evaluations.png`: AI Evaluations page with "Evaluations" active in navbar. Title "AI Evaluations" with a "View Statistics" outline button on the right (with chart icon). Below is an info-banner with a red info-circle icon and message: "Evaluations will be available once the AI evaluation service is connected." The banner has a light red/pink background with subtle red border.

- `thought_detail.png`: Thought Detail page with "Thoughts" active in navbar. A "Back to Thoughts" link with left arrow above the detail card. The detail-card has a header with "Thought Detail" title and Edit (outline warning) / Delete (red filled) buttons on the right. The detail-body shows the full thought content text, then a row of metadata: Author, Author Bio, Status (green "Approved" badge). Below that, another row: Thumbs Up (green bold "0"), Thumbs Down (red bold "0"), Created (formatted date/time), Updated (formatted date/time). Below the detail card is an "AI Evaluations" section heading with an info-banner stating "No evaluations available for this thought."

### Visual Insights:
- **Navbar design**: Red background (`#EE0000`), white brand text, nav links with Bootstrap Icons, active state shown as semi-transparent white pill background on the current page's nav link
- **Responsive navbar**: Mockups show desktop layout; Bootstrap toggler should be added for mobile breakpoints
- **Active nav state**: Each page clearly highlights its corresponding nav link (Dashboard, Thoughts, Ratings, Evaluations)
- **Table links**: Content column links appear in a red/coral color (matches `--rh-red` or similar), not the default black currently in CSS
- **Stat cards**: Use gradient backgrounds (red, green, red-dark) with white text, icons, and large bold numbers
- **Status cards**: Bordered cards with colored left/full border, subtle background tint, icon + label + count
- **Status badges**: Green pill for Approved, red pill for Rejected, yellow pill for In Review
- **Detail card**: Bordered card with header (title + action buttons) and body (content + metadata grid)
- **Info banners**: Light red background with red border, red info icon, used for placeholder/empty states
- **Sort buttons on Ratings**: Styled as individual rounded pill buttons (not a connected Bootstrap btn-group) -- the active button is filled red, inactive ones are outline red
- **Thought detail page**: Includes an "AI Evaluations" section below the detail card showing evaluation info or a placeholder info-banner
- **Footer**: Dark/black background, centered text, consistent across all pages
- **Fidelity level**: High-fidelity mockups -- these are polished designs from a Lovable preview app, intended to be matched closely

## Requirements Summary

### Functional Requirements
- Update all Qute templates to match the mockup designs using existing CSS classes from `styles.css`
- Add Bootstrap responsive navbar toggler (hamburger menu) for mobile viewports
- Implement active nav link state per page (Dashboard, Thoughts, Ratings, Evaluations)
- Add "AI Evaluations" section to thought detail page with info-banner placeholder
- Ensure all pages use the correct CSS component classes (stat-card, status-card, data-table-wrap, detail-card, form-card, status-badge, info-banner, etc.)
- No backend/Java code changes

### Templates to Update
1. **layout.html** - Add Bootstrap navbar-toggler with collapse, pass active page context for nav link highlighting
2. **DashboardResource/dashboard.html** - Verify stat cards, status cards, and recent activity table match mockup
3. **ThoughtResource/thoughts.html** - Verify table structure, pagination, "Create New Thought" button placement
4. **ThoughtResource/detail.html** - Add "AI Evaluations" section below detail card with info-banner
5. **ThoughtResource/create.html** - Verify form-wrapper and form-card usage
6. **ThoughtResource/edit.html** - Verify form-wrapper and form-card usage
7. **RatingsResource/ratings.html** - Update sort buttons from connected btn-group to individual rounded pill buttons
8. **EvaluationResource/evaluations.html** - Verify info-banner and "View Statistics" button match mockup
9. **EvaluationResource/stats.html** - Verify info-banner and "Back to Evaluations" button match mockup

### Key Design Differences Between Current Templates and Mockups
1. **Navbar**: Missing responsive toggler; no active state handling for nav links
2. **Table content links**: Mockups show red/coral colored links; current CSS has `color: var(--rh-black)` for `.data-table a` -- CSS may need a minor update or an additional class
3. **Ratings sort buttons**: Currently a connected `btn-group`; mockups show separated pill-style buttons
4. **Thought detail**: Missing "AI Evaluations" section below the detail card
5. **Active nav state**: Needs a mechanism to pass the current page name to layout.html so the correct nav link gets the `active` class

### Reusability Opportunities
- The existing CSS in `styles.css` already defines all needed component classes
- Bootstrap 5 responsive navbar patterns are standard and well-documented
- The `{#include layout}` Qute template inheritance pattern is already in place

### Scope Boundaries
**In Scope:**
- All 9 Qute template files (layout + 8 page templates)
- Adding Bootstrap responsive navbar toggler
- Adding nav link active state per page
- Adding "AI Evaluations" section to thought detail page
- Adjusting ratings sort button styling from btn-group to individual pills
- Minor CSS adjustments if needed (e.g., table link color to match mockups)

**Out of Scope:**
- Backend Java code changes (resources, models, clients)
- Adding new pages or routes
- Changing data/item counts or pagination behavior
- JavaScript functionality beyond Bootstrap's built-in toggler
- Database or API changes

### Technical Considerations
- **Active nav state mechanism**: The layout.html template needs to know which page is active. This can be achieved by passing an `activePage` variable from each resource's template data, or by using Qute's `{#insert}` blocks to inject the active class. Since no Java changes are allowed, the preferred approach is using Qute template sections (e.g., `{#insert activeNav}dashboard{/insert}` in each child template, with conditional class logic in layout.html)
- **Bootstrap navbar toggler**: Requires `navbar-expand-lg` (or similar breakpoint class) on the `<nav>` element, a `<button>` toggler, and wrapping nav links in a `collapse navbar-collapse` div. The Bootstrap JS bundle is already included in layout.html
- **Evaluations pages**: These are listed as deleted in git status but should be included in the reskin. They may need to be restored/recreated
- **CSS adjustments**: The `.data-table a` color may need to change from `var(--rh-black)` to `var(--rh-red)` to match mockups, and sort button styling may need minor CSS additions for separated pill style
