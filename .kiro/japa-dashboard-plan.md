# Japa Dashboard — Implementation Plan

## Story 1 — Data Model
- Create `JapaLog` entity: userId, malas, intensity (enum), duration, loggedAt, notes
- Create `JapaTarget` entity: userId, targetMalas, effectiveFrom (date) — allows target history
- Add JPA repositories for both
- Add DB migration (schema.sql or Liquibase)

## Story 2 — Japa Log API
- `POST /japa/log` — save a japa log entry (auth required)
- `GET /japa/log?date=` — get all entries for a specific day
- `PUT /japa/log/{id}` — edit an entry
- `DELETE /japa/log/{id}` — delete an entry (own entries only)

## Story 3 — Target API
- `POST /japa/target` — set/update daily target
- `GET /japa/target` — get current target

## Story 4 — Dashboard Analytics API
Endpoints to power the dashboard charts:
- `GET /japa/dashboard/summary` — today's total, streak, weekly/monthly/all-time totals
- `GET /japa/dashboard/heatmap?year=` — daily mala counts for calendar heatmap
- `GET /japa/dashboard/barchart?range=7|30` — daily totals for bar chart
- `GET /japa/dashboard/intensity?range=30` — intensity trend for line chart

## Story 5 — Admin API
- `GET /japa/admin/overview` — all users: today's count + current streak (admin only)
- `GET /japa/admin/user/{userId}/dashboard` — full dashboard data for a specific user (admin only)
- Add `ROLE_ADMIN` to `User` entity and enforce via Spring Security

## Story 6 — Frontend: Log Entry UI
- Log entry form on the dashboard page
- Fields: malas (number input), intensity (dropdown), duration, time, notes
- Show today's entries in a list with edit/delete

## Story 7 — Frontend: Dashboard UI
- Progress ring — today's malas vs target
- Streak counter (prominent)
- Summary cards — weekly / monthly / all-time
- Calendar heatmap (use a JS library — e.g., Cal-Heatmap)
- Bar chart — daily malas (Chart.js)
- Line chart — intensity trend (Chart.js)

## Story 8 — Frontend: Admin View
- Admin-only page: user list with today's count + streak
- Click through to individual user's dashboard

## Suggested Order
1 → 2 → 3 → 4 → 6 → 7 → 5 → 8

> Stories 1–4 are pure backend. Stories 6–7 are frontend. Story 5 and 8 (admin) can be done last.
> Note: Spring Security (ROLE_ADMIN) from the security plan is a prerequisite for Story 5.
