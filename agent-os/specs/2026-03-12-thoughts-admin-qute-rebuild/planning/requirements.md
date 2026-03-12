# Spec Requirements: Thoughts Admin Qute Template Rebuild

## Initial Description
Rebuild the Qute templates in the thoughts-admin Quarkus app so they are visually identical to the thoughts-admin-frontend React app. The React frontend uses Tailwind CSS while the Qute templates use Bootstrap 5 + custom CSS. The goal is to make the Qute templates produce HTML that looks visually identical to the React frontend output, matching the same design language, layout, and brand appearance (not pixel-perfect).

**Source of truth (React frontend):** `/Users/jeremyrdavis/Workspace/DevHub/thoughts-admin-frontend/`
**Target (Qute templates):** `/Users/jeremyrdavis/Workspace/DevHub/thoughts-admin/src/main/resources/templates/`
**CSS:** `/Users/jeremyrdavis/Workspace/DevHub/thoughts-admin/src/main/resources/META-INF/resources/css/styles.css`

## Requirements Discussion

### First Round Questions

**Q1:** I assume the goal is a pure CSS/HTML-level visual match -- same colors, spacing, typography, layout structure -- but NOT pixel-perfect rendering since Bootstrap 5 and Tailwind CSS produce slightly different default behaviors (e.g., form controls, button padding). Is "visually identical" acceptable as "same design language, layout, and brand appearance" rather than literally pixel-matched?
**Answer:** Yes, "same design language, layout, and brand appearance" is acceptable -- not pixel-perfect.

**Q2:** The React frontend uses Lucide icons. The current Qute templates use Bootstrap Icons. Should we continue using Bootstrap Icons with equivalent mappings to Lucide icons, or switch to Lucide icons via CDN?
**Answer:** Continue using Bootstrap Icons with equivalent mappings to Lucide icons.

**Q3:** The React frontend table links use `text-foreground` color (near-black) with underline on hover, while the current Qute CSS uses `color: var(--rh-red)` for table links. Should we match the React behavior?
**Answer:** Yes, match the React behavior -- dark/near-black table links with underline on hover.

**Q4:** The React frontend sort buttons on the Ratings page use rectangular buttons, but the current Qute templates use `rounded-pill` buttons. Should we switch to rectangular?
**Answer:** Yes, match the React frontend -- rectangular sort buttons, not rounded-pill.

**Q5:** The React ThoughtDetail page has a modal confirmation dialog (AlertDialog) for delete, while the Qute template uses a simple `confirm()` JavaScript dialog. Should we keep the simpler approach?
**Answer:** Yes, keep the simpler confirm() approach for the Qute version.

**Q6:** The `EvaluationResource` and its templates appear to be deleted in the current git status. Should we recreate these templates as part of this rebuild?
**Answer:** Yes, include the evaluations templates in the rebuild.

**Q7:** The React frontend container uses `max-width: 1400px` with `padding: 2rem`. Should we customize the Bootstrap container to approximate this?
**Answer:** Yes, customize the Bootstrap container to approximate 1400px max-width.

**Q8:** Should we skip client-side interactive features like live character counters on forms?
**Answer:** Yes, skip client-side interactive features like live character counters. Keep it server-rendered only.

### Existing Code to Reference

No similar existing features identified for reference beyond what is already in the codebase. The React frontend source code itself serves as the visual reference.

Key files in the React frontend to reference:
- `src/components/Layout.tsx` - Main layout with nav and footer
- `src/pages/Dashboard.tsx` - Dashboard with stat cards, status cards, recent activity table
- `src/pages/ThoughtsList.tsx` - Paginated thoughts table
- `src/pages/ThoughtDetail.tsx` - Thought detail with edit/delete and AI evaluations section
- `src/pages/ThoughtCreate.tsx` - Create form
- `src/pages/ThoughtEdit.tsx` - Edit form
- `src/pages/Ratings.tsx` - Ratings with sort buttons
- `src/pages/Evaluations.tsx` - Evaluations placeholder
- `src/pages/EvaluationStats.tsx` - Evaluation stats placeholder
- `src/components/StatusBadge.tsx` - Status badge component
- `src/index.css` - Tailwind CSS with custom properties and component classes

### Follow-up Questions
No follow-up questions were needed.

## Visual Assets

### Files Provided:
- `dashboard.png`: Screenshot of the React frontend dashboard showing the red navbar with "Thoughts Admin" branding, three gradient stat cards (Total Thoughts in red, Total Thumbs Up in green, Total Thumbs Down in red-orange), three bordered status cards (Approved with green border, Rejected with green border tint, In Review with yellow border tint), and a Recent Activity table with dark header row, near-black content links, status badges, and date column.
- `thoughts.png`: Screenshot of the Thoughts list page showing a "Create New Thought" red button in top-right, a full-width table with dark (#212121) header row, content links in red (note: this may represent current state, the target should be near-black links), Author, Status (pill badges), thumbs up/down columns in green/red, and Created date column.
- `ratings.png`: Screenshot of the Ratings Overview page showing three rectangular sort buttons (Most Rated filled red, Most Liked and Most Disliked outlined), and a table with Content, thumbs up emoji, thumbs down emoji, and Net Score columns. Content links appear in red/dark style.
- `evaluations.png`: Screenshot of the AI Evaluations page showing a "View Statistics" outline button in top-right, and a light red/pink info banner with a red info circle icon and placeholder text about evaluation service not being connected.
- `thought_detail.png`: Screenshot of the Thought Detail page showing "Back to Thoughts" outline button, a bordered card with "Thought Detail" header, Edit (outline-warning) and Delete (red/danger) buttons, the thought content text, Author/Author Bio/Status fields in a 3-column grid, Thumbs Up/Thumbs Down/Created/Updated in a 4-column grid, and an "AI Evaluations" section below with a light red info banner.

### Visual Insights:
- Navigation bar: Solid Red Hat red (#EE0000) background, white text, active nav item has semi-transparent white background highlight
- Stat cards: Use gradient backgrounds (red-to-dark-red, green-to-dark-green, red-to-dark-red), white text, icon + label + large number layout
- Status cards: Bordered cards with 2px colored border and very light tinted background (5% opacity), icon + label + count layout
- Tables: Dark header row (#212121 background, white text), content links should be near-black with underline on hover, alternating row borders
- Status badges: Pill-shaped (border-radius: 9999px), small text, colored backgrounds (green for Approved, red for Rejected, yellow for In Review)
- Forms: Centered max-width container (~42rem), bordered card wrapper, standard form labels and inputs
- Info banners: Light red/pink background with red border at ~20% opacity, red info circle icon
- Footer: Dark (#151515) background, white text at 80% opacity, centered
- Container: ~1400px max-width with 2rem padding
- Typography: Red Hat Display for headings (700 weight, -0.02em letter-spacing), Red Hat Text for body
- Fidelity level: High-fidelity screenshots from the live React application

## Requirements Summary

### Functional Requirements
- Rebuild all Qute templates to visually match the React frontend design
- Templates to update: layout.html, dashboard.html, thoughts.html, detail.html, create.html, edit.html, ratings.html
- Templates to recreate: evaluations.html, stats.html (EvaluationResource)
- Update CSS (styles.css) to match React frontend's design tokens and component styles
- Keep all existing Qute template logic (data binding, conditionals, loops) intact
- Maintain server-side rendering approach (no client-side JavaScript for interactivity)

### Specific CSS/Style Changes Required
1. **Table links**: Change from `color: var(--rh-red)` to near-black (`color: var(--rh-black)` or `color: #151515`) with `text-decoration: underline` on hover
2. **Sort buttons (Ratings)**: Remove `rounded-pill` class, use standard rectangular buttons
3. **Container max-width**: Set to approximately 1400px with 2rem padding to match React layout
4. **Keep existing styles**: Stat cards, status cards, status badges, navbar, footer, form cards, detail cards, info banners are already closely matching and need only minor tweaks if any
5. **Thumbs up/down colors in tables**: Use green (success) for thumbs up and red (destructive) for thumbs down without bold weight (React uses `text-success` / `text-destructive` without extra font-weight)

### Reusability Opportunities
- The existing CSS custom properties (`:root` variables) already closely match the React frontend's design tokens
- Stat card, status card, status badge, detail card, form card, and info banner CSS classes are already well-structured and closely matching
- Bootstrap Icons can continue to be used with equivalent Lucide icon mappings

### Scope Boundaries
**In Scope:**
- Updating all Qute HTML templates to match React frontend layout and structure
- Updating CSS to match design tokens, colors, spacing, and component styles
- Recreating EvaluationResource templates (evaluations.html, stats.html)
- Icon mapping from Lucide to Bootstrap Icons
- Container width adjustment to ~1400px
- Table link color change (red to near-black)
- Sort button shape change (pill to rectangular)

**Out of Scope:**
- Client-side JavaScript features (live character counters, toast notifications, AlertDialog modals)
- React Query / client-side data fetching behavior
- Any changes to Java resource classes or backend logic
- Pixel-perfect rendering match between Bootstrap and Tailwind
- Dark mode support
- Any new pages or features not present in the React frontend

### Technical Considerations
- Bootstrap 5 CDN is already loaded and should continue to be used
- Bootstrap Icons CDN is already loaded and should continue to be used
- Red Hat Display and Red Hat Text Google Fonts are already loaded
- Custom CSS at `/css/styles.css` overrides Bootstrap defaults for Red Hat branding
- Qute `{#include layout}` template inheritance pattern must be maintained
- `@CheckedTemplate` convention maps template files to Java resource methods
- The thoughts-admin app has no database -- it calls thoughts-backend via MicroProfile REST Client
- The EvaluationResource Java class and its route handling may need to be restored (currently deleted in git status) to support the recreated templates
