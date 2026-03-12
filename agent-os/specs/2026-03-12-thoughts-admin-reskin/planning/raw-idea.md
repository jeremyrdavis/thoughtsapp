# Raw Idea: Re-skin thoughts-admin Quarkus App

## User Description

Re-skinning the thoughts-admin Quarkus app. The CSS is already in place, and the user has screenshots of a mockup to use as reference. The app uses Qute templates with Bootstrap 5 and needs its templates updated to match the mockup designs.

## Context

- Service: thoughts-admin (Quarkus admin UI)
- Port: 8081
- Template engine: Qute
- UI framework: Bootstrap 5
- CSS: Already implemented
- Reference: User has mockup screenshots
- Scope: Update Qute templates to match mockup designs

## Current Template Structure

Located in `thoughts-admin/src/main/resources/templates/`:
- `layout.html` - Base template with Bootstrap 5
- `DashboardResource/dashboard.html`
- `ThoughtResource/thoughts.html`
- `ThoughtResource/detail.html`
- `ThoughtResource/create.html`
- `ThoughtResource/edit.html`
- `RatingsResource/ratings.html`

## Task

Update all Qute templates to match the new mockup designs while leveraging the existing CSS implementation.
