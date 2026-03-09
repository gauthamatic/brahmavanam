# Brahmavanam - Project Context

## Overview
Spring Boot web app for managing bookings at Brahmavanam, a spiritual retreat/meditation center.

## Tech Stack
- Java 17, Spring Boot 3.4.0
- Spring Data JPA + Hibernate
- Thymeleaf (server-side templates)
- MySQL (cloud), H2 (local dev)
- Lombok, Gradle
- Version: 0.0.17-SNAPSHOT

## Data Model

### User
- id, firstname, lastname, email_id, password (plain text — no hashing yet)

### Event
- id, title, start_date (String), end_date (String), color, text_color
- FK → User, FK → RRule

### RRule (Recurrence Rule)
- id, freq, interval_value, byweekday (comma-separated), dtstart

## Booking Business Rules (CalendarService)
1. Cannot book a past date
2. Only one booking allowed per date
3. A user cannot have two active (future) bookings simultaneously
4. Minimum 90 days gap between a user's bookings
5. Cannot book for just one day (must be 2+ days, or come on Sunday)
6. Users can only delete their own future events — past events are immutable

## Pre-seeded Recurring Events
- "Open to All" — every Sunday (green)
- "No Bookings allowed" — every Wed & Thu (orange)

## Auth
- Custom session-based login (Spring Security imported but disabled)
- Signup requires a master password (`master.password` in properties)
- Session key: `"user"` stores the user's email

## Profiles
- `local` — H2 in-memory DB
- `cloud` — MySQL at private IP (AWS EC2), credentials in `application-cloud.properties`

## Deployment
- Built jar is manually copied to EC2 instance at `/home/ec2-user/brahmavanam/jar/`
- MySQL is installed directly on the EC2 instance
- MySQL data is persisted on an attached EBS volume
- App is started with:
  ```
  java -Djava.net.preferIPv4Stack=true -Dspring.profiles.active=cloud -jar /home/ec2-user/brahmavanam/jar/brahmavanam-0.0.17-SNAPSHOT.jar & disown
  ```
- `& disown` detaches the process from the terminal session so it keeps running after logout

## Routes
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` `/home` | Home page |
| GET | `/login` | Login page |
| POST | `/login` | Authenticate user |
| GET | `/signup` | Signup page |
| POST | `/signup` | Register user (requires master password) |
| GET | `/logout` | Invalidate session |
| GET | `/forgotPassword` | Forgot password page (not implemented) |
| GET | `/calendar` | Calendar booking page |
| GET | `/events` | Fetch all events (JSON) |
| POST | `/events` | Save new event (JSON) |
| DELETE | `/events/{id}` | Delete event (auth required) |
| GET | `/user` | Get current session user (JSON) |
| GET | `/namana` | Redirect to WordPress blog |

## Templates
- `home.html` — landing page
- `calendarHome.html` — main calendar UI
- `login.html` — login form
- `signup.html` — signup form
- `forgot_password.html` — placeholder (not implemented)

## Package Structure
```
com.brahmavanam
├── BrahmavanamApplication.java
├── controller/
│   ├── Router.java
│   └── LoginController.java
├── calendar/
│   ├── model/        — Event, User, RRule
│   ├── dto/          — EventDTO, UserDTO, RRuleDTO
│   ├── service/      — CalendarService, UserService
│   ├── repository/   — EventRepository, UserRepository, RRuleRepository
│   └── converter/    — StringArrayConverter
└── exceptionHandler/
    └── GlobalExceptionHandler.java
```

## Pending Improvements

### Security (High Priority)
- Plain text passwords — need BCrypt in `UserService.validateUser()`
- `master.password` hardcoded in properties — move to environment variable
- No auth check on `POST /events` — anyone can create a booking without login

### Pending Features
- Forgot password — template exists (`forgot_password.html`) but not implemented. Needs Spring Mail + reset token flow.

### Code Quality
- `startDate`/`endDate` on `Event` stored as `String` instead of `LocalDate` — causes `.substring(0, 23)` parsing hacks in `CalendarService`
- `EventRepository.findByUserIdAndStartDateAfter()` — defined but never used (dead code)
- `getEventDetails()` in `CalendarService` — returns empty list, never used (dead code)
- DTO conversion (`convertToDTO`, `convertToEntity`) in `Router.java` — should be in a dedicated mapper class
- `UserService.findUserByUsername()` — calls `.get()` on Optional without null check
- No input validation (`@Valid` / `@NotNull`) on DTOs or request params
