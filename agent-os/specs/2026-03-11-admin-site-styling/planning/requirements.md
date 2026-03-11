# Spec Requirements: Admin Site Styling

## Initial Description
Styling the admin site - improving the visual design and styling of the Quarkus admin site that uses Qute templating and Bootstrap 5.

## Requirements Discussion

### First Round Questions

**Q1:** Should the color palette align with Red Hat branding (reds, blacks, whites) since this is a Red Hat demo application, or a different palette? Should the navbar shift from generic `bg-dark` to a Red Hat-branded dark with accent colors?
**Answer:** Use Red Hat branding. Great idea.

**Q2:** Should we add a custom `styles.css` file served from `src/main/resources/META-INF/resources/css/` (Quarkus static resource path) layered on top of Bootstrap, or use Bootstrap Sass customization or a Bootswatch theme?
**Answer:** Custom CSS file layered on top of Bootstrap.

**Q3:** Should we add custom web fonts (Red Hat Display / Red Hat Text from Google Fonts) or stick with system fonts and improve sizing, weight, and spacing?
**Answer:** Use the Red Hat fonts from Google Fonts (Red Hat Display / Red Hat Text).

**Q4:** Should the dashboard stat cards be enhanced with more visual polish -- gradients, icons (e.g., Bootstrap Icons via CDN), shadow/depth effects, or a different card layout?
**Answer:** Yes, enhance them with more visual polish (gradients, icons, shadows, etc.).

**Q5:** Should we soften the table styling (lighter headers, custom hover colors, better padding) or keep the high-contrast dark headers?
**Answer:** Keep high contrast dark headers for now.

**Q6:** Should we add a branded footer, or is a footer unnecessary for this admin tool?
**Answer:** Yes, add a branded footer.

**Q7:** Is this purely CSS/HTML template changes with no backend Java code modifications?
**Answer:** Initially said backend changes were in scope, but revised to confirm this is purely CSS/HTML template changes -- no backend Java code modifications.

**Q8:** Should we avoid changing page layouts or component structure and focus only on visual polish, or is restructuring also in scope?
**Answer:** Initially said restructuring was in scope, but revised to keep the existing page layouts and component structure as-is. Visual polish only.

### Existing Code to Reference
No similar existing features identified for reference. User confirmed no existing code reuse is needed.

### Follow-up Questions

**Follow-up 1:** What does "split the pages up into logical sections" mean specifically -- distinct visual panels on existing pages, or entirely separate pages/routes?
**Answer:** User changed their mind -- no restructuring. Keep the existing page layouts and component structure as-is.

**Follow-up 2:** Should the dashboard be restructured into distinct visual sections?
**Answer:** No restructuring. Keep the current dashboard layout.

**Follow-up 3:** Should navigation be enhanced with breadcrumbs or sub-navigation?
**Answer:** No navigation changes. Keep the current simple navbar.

## Visual Assets

### Files Provided:
No visual assets provided. (Confirmed via filesystem check -- no image files found in `/Users/jeremyrdavis/Workspace/DevHub/agent-os/specs/2026-03-11-admin-site-styling/planning/visuals/`.)

### Visual Insights:
N/A -- no visual assets to analyze.

## Requirements Summary

### Functional Requirements
- Apply Red Hat branding color palette across the entire admin site (navbar, cards, badges, buttons, links, etc.)
- Add a custom CSS file at `src/main/resources/META-INF/resources/css/styles.css` that layers on top of the existing Bootstrap 5 CDN include
- Import and apply Red Hat Display (for headings) and Red Hat Text (for body copy) from Google Fonts via the layout template
- Enhance dashboard stat cards with gradients, subtle shadows/depth effects, and icons (e.g., Bootstrap Icons via CDN)
- Keep the existing high-contrast dark table headers (`table-dark` class) across all list pages
- Add a branded footer to the base layout template (e.g., "Positive Thoughts Admin - Red Hat Demo" or similar branding text)
- Improve overall visual polish: spacing, padding, color consistency, and typographic hierarchy across all pages

### Reusability Opportunities
None identified. User confirmed no existing code reuse is needed.

### Scope Boundaries
**In Scope:**
- Custom CSS file creation and integration
- Red Hat branding color palette application
- Google Fonts integration (Red Hat Display, Red Hat Text)
- Dashboard stat card visual enhancements (gradients, icons, shadows)
- Bootstrap Icons CDN integration for card icons
- Branded footer addition to base layout template
- Typography improvements (font family, sizing, weight, spacing)
- Visual polish across all existing pages (navbar, cards, tables, forms, badges, buttons, alerts)

**Out of Scope:**
- Backend Java code changes (no resource class or entity modifications)
- Page layout restructuring or component reorganization
- Navigation changes (no breadcrumbs, no sub-navigation, no dropdown menus)
- New pages or routes
- JavaScript functionality changes
- Responsive breakpoint changes or mobile-specific redesign
- Replacing Bootstrap with another CSS framework
- Bootstrap Sass compilation or Bootswatch themes
- Authentication, authorization, or any non-visual features

### Technical Considerations
- Custom CSS must be served from Quarkus static resources path: `src/main/resources/META-INF/resources/css/`
- Google Fonts link tags must be added to the `<head>` in `layout.html`
- Bootstrap Icons CDN link must be added to the `<head>` in `layout.html`
- The custom CSS file must be loaded after Bootstrap CSS to properly override defaults
- All existing Bootstrap utility classes remain in use; custom CSS adds overrides and enhancements
- The footer must be added to the base `layout.html` template so it appears on all pages
- Red Hat brand colors for reference: Red Hat Red (#EE0000), black (#151515), white (#FFFFFF), with supporting grays and accent colors from the Red Hat brand guidelines
- Current templates affected: `layout.html`, `dashboard.html`, `thoughts.html`, `detail.html`, `create.html`, `edit.html`, `ratings.html`, `evaluations.html`, `stats.html`
