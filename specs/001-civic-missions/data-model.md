# Data Model: Civic Missions MVP

## Entities

### Mission

Represents a civic action opportunity published by an organizer.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | Auto-generated | Unique identifier |
| title | String | Required, 3-100 chars | Mission name |
| description | String | Required, 10-2000 chars | What the mission involves |
| theme | Enum | Required | `MOBILITY`, `RESOURCES`, `FOOD` |
| dateTime | ISO DateTime | Required, future | When the mission occurs |
| location | String | Required, 3-200 chars | Text address (no map) |
| maxParticipants | Int | Required, 1-100 | Maximum signups allowed |
| currentParticipants | Int | Auto-calculated | Current signup count |
| status | Enum | Required | `DRAFT`, `PUBLISHED`, `FULL`, `CANCELLED`, `DONE` |
| whatToBring | String | Optional, 0-500 chars | Items participants should bring |
| organizerId | UUID | Required | Reference to creator |
| createdAt | ISO DateTime | Auto | Creation timestamp |
| updatedAt | ISO DateTime | Auto | Last modification |
| completionSummary | String | Optional | Summary after completion (for DONE status) |
| actualParticipants | Int | Optional | Count after completion |

**State Transitions**:
```
DRAFT → PUBLISHED (organizer publishes)
PUBLISHED → FULL (auto when currentParticipants = maxParticipants)
PUBLISHED → CANCELLED (organizer cancels)
PUBLISHED → DONE (organizer marks complete)
FULL → CANCELLED (organizer cancels)
FULL → DONE (organizer marks complete)
```

---

### Signup

Represents a citizen's registration for a mission.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | Auto-generated | Unique identifier |
| missionId | UUID | Required | Reference to mission |
| participantName | String | Required, 2-100 chars | Citizen's name |
| contactEmail | String | Optional* | Email address |
| contactPhone | String | Optional* | Phone number |
| signedUpAt | ISO DateTime | Auto | Registration timestamp |
| status | Enum | Required | `CONFIRMED`, `CANCELLED` |

*At least one of contactEmail or contactPhone is required.

**State Transitions**:
```
CONFIRMED → CANCELLED (participant cancels)
```

---

### Organizer

Represents a user who creates and manages missions.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | Auto-generated | Unique identifier |
| username | String | Required, unique, 3-50 chars | Login username |
| passwordHash | String | Required | Hashed password |
| name | String | Required, 2-100 chars | Display name |
| organization | String | Optional | Municipality or association name |
| createdAt | ISO DateTime | Auto | Creation timestamp |
| lastLoginAt | ISO DateTime | Auto-updated | Last login timestamp |

---

### MissionArchive

Read-only projection of completed missions for public viewing.

Same fields as Mission, with:
- `status` always `DONE`
- `actualParticipants` always populated
- `completionSummary` always populated
- No edit capabilities

---

## Validation Rules

### Mission Validation

1. `title`: 3-100 characters, not blank
2. `description`: 10-2000 characters
3. `dateTime`: Must be in the future (for PUBLISHED status)
4. `maxParticipants`: 1-100
5. `location`: 3-200 characters, plain text address
6. Status transitions must follow allowed transitions only

### Signup Validation

1. At least one of `contactEmail` or `contactPhone` must be provided
2. `contactEmail` must be valid email format if provided
3. `contactPhone` must be valid phone format if provided (10-15 digits)
4. Cannot sign up if mission is FULL, CANCELLED, or DONE
5. Cannot sign up twice for same mission with same contact

### Organizer Validation

1. `username`: Alphanumeric + underscore, 3-50 chars, unique
2. `password`: Minimum 8 characters
3. `name`: 2-100 characters

---

## API Contracts

### Public Endpoints (No Auth)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/missions` | List published missions (with filters) |
| GET | `/api/v1/missions/{id}` | Get mission details |
| GET | `/api/v1/missions/archive` | List completed missions |
| POST | `/api/v1/missions/{id}/signup` | Sign up for a mission |

### Organizer Endpoints (Auth Required)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/auth/login` | Organizer login |
| POST | `/api/v1/auth/logout` | Organizer logout |
| GET | `/api/v1/organizer/missions` | List organizer's missions |
| POST | `/api/v1/organizer/missions` | Create new mission |
| PUT | `/api/v1/organizer/missions/{id}` | Update mission |
| PATCH | `/api/v1/organizer/missions/{id}/status` | Update mission status |
| GET | `/api/v1/organizer/missions/{id}/signups` | List signups for mission |
| GET | `/api/v1/organizer/missions/{id}/export` | Export signups as CSV |

---

## Data Privacy

Per Constitution Principle VII (Data Privacy by Design):

1. **Minimum Data**: Only name + contact (email OR phone) required for signup
2. **Purpose Stated**: Clear explanation shown at signup why data is collected
3. **Export**: Organizers can export participant data as CSV
4. **Deletion**: Participants can request data deletion via contact form
5. **Retention**: Completed mission data retained for transparency; signups archived with mission

---

## File Storage Structure

```
server/data/
├── missions.json      # All missions
├── signups.json      # All signups
└── organizers.json   # Organizer accounts (hashed passwords)
```

In-memory cache loaded at server startup, persisted on changes.
