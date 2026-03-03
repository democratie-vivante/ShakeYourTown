# API Contracts: Civic Missions MVP

## Version 1.0

Base URL: `/api/v1`

All responses include `Content-Type: application/json`

---

## Public Endpoints

### List Missions

```
GET /missions?theme={theme}&status={status}&upcoming={bool}
```

**Query Parameters**:
| Param | Type | Required | Description |
|-------|------|----------|-------------|
| theme | string | No | Filter: `MOBILITY`, `RESOURCES`, `FOOD` |
| status | string | No | Filter: `PUBLISHED`, `FULL`, `DONE` |
| upcoming | bool | No | `true` = future only, `false` = all |

**Response 200**:
```json
{
  "missions": [
    {
      "id": "uuid",
      "title": "Planting Day at Community Garden",
      "description": "Help us plant vegetables...",
      "theme": "FOOD",
      "dateTime": "2026-04-15T10:00:00Z",
      "location": "123 Main Street, Town Center",
      "maxParticipants": 10,
      "currentParticipants": 5,
      "status": "PUBLISHED",
      "whatToBring": "Gloves, water bottle"
    }
  ]
}
```

---

### Get Mission Details

```
GET /missions/{id}
```

**Response 200**:
```json
{
  "id": "uuid",
  "title": "Planting Day at Community Garden",
  "description": "Help us plant vegetables...",
  "theme": "FOOD",
  "dateTime": "2026-04-15T10:00:00Z",
  "location": "123 Main Street, Town Center",
  "maxParticipants": 10,
  "currentParticipants": 5,
  "status": "PUBLISHED",
  "whatToBring": "Gloves, water bottle",
  "organizerContact": "organizer@town.fr"
}
```

---

### List Archive

```
GET /missions/archive?theme={theme}&period={period}
```

**Query Parameters**:
| Param | Type | Required | Description |
|-------|------|----------|-------------|
| theme | string | No | Filter: `MOBILITY`, `RESOURCES`, `FOOD` |
| period | string | No | `month`, `quarter`, `year` |

**Response 200**:
```json
{
  "missions": [
    {
      "id": "uuid",
      "title": "Winter Bike Repair Workshop",
      "theme": "MOBILITY",
      "dateTime": "2026-01-20T14:00:00Z",
      "status": "DONE",
      "actualParticipants": 8,
      "completionSummary": "Repaired 12 bikes"
    }
  ]
}
```

---

### Sign Up for Mission

```
POST /missions/{id}/signup
```

**Request Body**:
```json
{
  "participantName": "John Doe",
  "contactEmail": "john@example.com",
  "contactPhone": null
}
```

**Privacy Notice**: Display before submission: "We collect your contact info only to coordinate this mission. We'll use it to confirm your signup and notify you of any changes."

**Response 201**:
```json
{
  "success": true,
  "signupId": "uuid",
  "message": "You're signed up! Confirmation sent to john@example.com"
}
```

**Response 400** (validation error):
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Email or phone required",
  "fields": ["contactEmail", "contactPhone"]
}
```

**Response 409** (full/cancelled):
```json
{
  "error": "MISSION_FULL",
  "message": "This mission is now full"
}
```

---

## Authenticated Endpoints

All authenticated endpoints require:
- Header: `Cookie: session={token}`
- Or Header: `Authorization: Bearer {token}`

### Login

```
POST /auth/login
```

**Request Body**:
```json
{
  "username": "admin",
  "password": "changeme123"
}
```

**Response 200**:
```json
{
  "success": true,
  "organizer": {
    "id": "uuid",
    "name": "Admin User",
    "organization": "Town Hall"
  }
}
```

**Response 401**:
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "Invalid username or password"
}
```

---

### List Organizer's Missions

```
GET /organizer/missions
```

**Response 200**:
```json
{
  "missions": [
    {
      "id": "uuid",
      "title": "Planting Day",
      "status": "PUBLISHED",
      "currentParticipants": 5,
      "dateTime": "2026-04-15T10:00:00Z"
    }
  ]
}
```

---

### Create Mission

```
POST /organizer/missions
```

**Request Body**:
```json
{
  "title": "Planting Day at Community Garden",
  "description": "Help us plant vegetables for the spring harvest",
  "theme": "FOOD",
  "dateTime": "2026-04-15T10:00:00Z",
  "location": "123 Main Street, Town Center",
  "maxParticipants": 10,
  "whatToBring": "Gloves, water bottle"
}
```

**Response 201**:
```json
{
  "id": "uuid",
  "status": "DRAFT"
}
```

---

### Update Mission

```
PUT /organizer/missions/{id}
```

**Request Body** (partial update allowed):
```json
{
  "title": "Updated Title",
  "maxParticipants": 15
}
```

**Response 200**:
```json
{
  "id": "uuid",
  "status": "DRAFT"
}
```

---

### Update Mission Status

```
PATCH /organizer/missions/{id}/status
```

**Request Body**:
```json
{
  "status": "PUBLISHED"
}
```

Valid transitions: `DRAFT` → `PUBLISHED`, `PUBLISHED/FULL` → `CANCELLED`, `PUBLISHED/FULL` → `DONE`

**Response 200**:
```json
{
  "id": "uuid",
  "status": "PUBLISHED"
}
```

---

### List Signups

```
GET /organizer/missions/{id}/signups
```

**Response 200**:
```json
{
  "signups": [
    {
      "id": "uuid",
      "participantName": "John Doe",
      "contactEmail": "john@example.com",
      "contactPhone": "+33123456789",
      "signedUpAt": "2026-03-01T10:00:00Z",
      "status": "CONFIRMED"
    }
  ]
}
```

---

### Export Signups CSV

```
GET /organizer/missions/{id}/export
```

**Response 200**:
```
Content-Type: text/csv
Content-Disposition: attachment; filename="mission-{id}-signups.csv"

Participant Name,Email,Phone,Signed Up
John Doe,john@example.com,+33123456789,2026-03-01T10:00:00Z
```

---

## Error Codes

| Code | Description |
|------|-------------|
| VALIDATION_ERROR | Request body validation failed |
| NOT_FOUND | Resource not found |
| UNAUTHORIZED | Authentication required |
| FORBIDDEN | Insufficient permissions |
| MISSION_FULL | Mission at capacity |
| MISSION_CANCELLED | Mission was cancelled |
| INVALID_TRANSITION | Status transition not allowed |
| DUPLICATE_SIGNUP | Already signed up |
