# Spring Security Implementation Plan

## Story 1 — BCrypt Password Hashing
**What:** Hash passwords on signup, compare hashed on login.
- Add `PasswordEncoder` bean (BCrypt)
- Update `UserService.saveUser()` to encode password
- Update `UserService.validateUser()` to use `passwordEncoder.matches()`
- Migrate existing plain text passwords in DB

> No Spring Security config needed — isolated change.

---

## Story 2 — Spring Security Filter Chain
**What:** Replace manual session checks with Spring Security.
- Create `SecurityConfig` class with `SecurityFilterChain` bean
- Configure public routes: `GET /`, `/home`, `/login`, `/signup`, `/forgotPassword`, `/namana`, `GET /events`
- Configure protected routes: `POST /events`, `DELETE /events/{id}`, `/calendar`
- Set login page to `/login`, default success URL to `/home`
- Set logout URL to `/logout`, redirect to `/home`

---

## Story 3 — UserDetailsService Integration
**What:** Wire the existing `User` entity into Spring Security's auth mechanism.
- Implement `UserDetailsService` — load user by email from `UserRepository`
- Replace manual `LoginController.login()` POST with Spring Security's form login
- Remove manual session attribute (`session.setAttribute("user", email)`)

---

## Story 4 — Secure POST /events
**What:** Bind the logged-in user to event creation automatically.
- Remove `user` field from `EventDTO` request body (security risk)
- In `Router.saveEvents()`, get current user from `SecurityContextHolder` instead of request body
- Update `convertToEntity()` accordingly

---

## Story 5 — Move Master Password to Environment Variable
**What:** Remove hardcoded `master.password` from properties file.
- Set as environment variable on EC2
- Reference via `${MASTER_PASSWORD}` in properties
- Update GitHub Actions secrets if needed

---

## Suggested Order
1 → 2 → 3 → 4 → 5

> Stories 1 and 5 are independent. Stories 2 and 3 are tightly coupled — do together.
