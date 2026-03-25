# Japa Dashboard — Requirements

## Japa Log Entry
Each log entry captures:
- **Malas count** — whole number (1 mala = 108 repetitions)
- **Intensity** — enum: `DISTRACTED / MODERATE / FOCUSED / DEEP`
- **Duration** — how long the session took (in minutes)
- **Logged time** — actual timestamp of the session
- **Notes** — free-text reflections
- **User** — FK to user

> Multiple entries per day allowed.

## Daily Target
- User can set a daily mala target (optional — free logging also supported)
- Target is changeable over time (stored with effective date so history is preserved)

## User Dashboard
- Today's malas vs target (progress ring/bar)
- Current streak & longest streak (consecutive days with at least 1 log)
- Weekly / monthly / all-time total malas
- Average intensity this week
- Calendar heatmap — color intensity = malas done that day (like GitHub contributions)
- Bar chart — daily malas over last 7 / 30 days
- Line chart — intensity trend over time

## Admin View
- Overview list of all users: today's count + current streak (leaderboard style)
- Ability to drill into any individual user's full dashboard

## Access Control
- Each user sees only their own dashboard
- Admin role can see all users' dashboards
