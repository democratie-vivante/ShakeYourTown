# Research: Civic Missions MVP

## Storage Solution for MVP

**Decision**: File-based JSON storage with in-memory caching

### Rationale

Given the constraints:
- Small municipality (~3,800 residents)
- Minimal external dependencies (per user requirement)
- ~20-50 missions/year, low data volume
- Offline-capable desirable

**Chosen Solution**: File-based JSON storage
- Uses Kotlin stdlib (no external dependencies)
- JSON format for human readability and easy debugging
- In-memory caching for performance
- Simple backup via file copy
- Can be extended to SQLite later if needed

### Alternatives Considered

| Alternative | Why Rejected |
|-------------|--------------|
| In-memory only | Data lost on restart - unacceptable for civic app |
| SQLite | Requires external dependency, more complex |
| PostgreSQL | Overkill for MVP, requires external server |
| Room | Android-only, external dependency |

---

## API Versioning Strategy

**Decision**: URL-based versioning (`/api/v1/`)

### Rationale

- Simple to implement with Ktor
- Clear contract boundary
- Easy to document and test
- Supports future migration paths

### Contract Format

- RESTful JSON API
- OpenAPI 3.0 specification (manual documentation for MVP)
- Version in URL path: `/api/v1/missions`

---

## Authentication Approach

**Decision**: Session-based auth for organizers using secure cookies

### Rationale

- Back-office is small number of users (municipality staff + association leaders)
- No external identity providers needed
- Simple to implement with Ktor
- Secure session management available in Ktor

### Organizer Credentials

- Username/password stored hashed (bcrypt from stdlib not available - use simple hash for MVP)
- Session token in HTTP-only secure cookie
- Sessions expire after 8 hours

---

## Notification Strategy

**Decision**: Email-based notifications using external SMTP relay

### Rationale

- All participants provide email or phone
- Email is most universal (SMS requires external service)
- Municipality likely has existing email infrastructure
- SMTP relay can be configured without code changes

### Implementation

- Ktor does not include SMTP - use runtime configuration for SMTP server
- Notification queue for retry on failure
- Templates for: signup confirmation, cancellation, changes

---

## Summary

All [NEEDS CLARIFICATION] items resolved:

| Item | Resolution |
|------|------------|
| Storage | File-based JSON with in-memory cache |
| API Versioning | URL-based (/api/v1/) |
| Authentication | Session-based with secure cookies |
| Notifications | Email via configurable SMTP relay |
