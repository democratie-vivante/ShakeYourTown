# API Contracts: Civic Missions MVP

## Version 1.0

Base URL: `/api/v1`

All responses include `Content-Type: application/json`

**Locale**: All user-facing strings (titles, descriptions, messages, error messages) MUST be in French (fr-FR). Technical field names and enum values remain in English.

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
      "title": "Journée plantation au jardin partagé",
      "description": "Aidez-nous à planter des légumes...",
      "theme": "FOOD",
      "dateTime": "2026-04-15T10:00:00Z",
      "location": "123 rue de la Mairie, Centre-ville",
      "maxParticipants": 10,
      "currentParticipants": 5,
      "status": "PUBLISHED",
      "whatToBring": "Gants, bouteille d'eau"
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
  "title": "Journée plantation au jardin partagé",
  "description": "Aidez-nous à planter des légumes...",
  "theme": "FOOD",
  "dateTime": "2026-04-15T10:00:00Z",
  "location": "123 rue de la Mairie, Centre-ville",
  "maxParticipants": 10,
  "currentParticipants": 5,
  "status": "PUBLISHED",
  "whatToBring": "Gants, bouteille d'eau",
  "organizerContact": "organisateur@mairie.fr"
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
      "title": "Atelier réparation vélos d'hiver",
      "theme": "MOBILITY",
      "dateTime": "2026-01-20T14:00:00Z",
      "status": "DONE",
      "actualParticipants": 8,
      "completionSummary": "12 vélos réparés"
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
  "participantName": "Jean Dupont",
  "contactEmail": "jean@example.fr",
  "contactPhone": null
}
```

**Privacy Notice**: Display before submission: "Nous collectons vos coordonnées uniquement pour organiser cette mission. Elles seront utilisées pour confirmer votre inscription et vous informer de tout changement."

**Response 201**:
```json
{
  "success": true,
  "signupId": "uuid",
  "message": "Inscription confirmée ! Une confirmation a été envoyée à jean@example.fr"
}
```

**Response 400** (validation error):
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Un email ou un numéro de téléphone est requis",
  "fields": ["contactEmail", "contactPhone"]
}
```

**Response 409** (full/cancelled):
```json
{
  "error": "MISSION_FULL",
  "message": "Cette mission est complète"
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
    "name": "Utilisateur Admin",
    "organization": "Mairie"
  }
}
```

**Response 401**:
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "Identifiant ou mot de passe invalide"
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
      "title": "Journée plantation",
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
  "title": "Journée plantation au jardin partagé",
  "description": "Aidez-nous à planter des légumes pour la récolte de printemps",
  "theme": "FOOD",
  "dateTime": "2026-04-15T10:00:00Z",
  "location": "123 rue de la Mairie, Centre-ville",
  "maxParticipants": 10,
  "whatToBring": "Gants, bouteille d'eau"
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
  "title": "Titre mis à jour",
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
      "participantName": "Jean Dupont",
      "contactEmail": "jean@example.fr",
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

Nom du participant,Email,Téléphone,Inscrit le
Jean Dupont,jean@example.fr,+33123456789,2026-03-01T10:00:00Z
```

---

## Error Codes

| Code | Description | User-facing message (fr) |
|------|-------------|--------------------------|
| VALIDATION_ERROR | Request body validation failed | "Erreur de validation" |
| NOT_FOUND | Resource not found | "Ressource introuvable" |
| UNAUTHORIZED | Authentication required | "Authentification requise" |
| FORBIDDEN | Insufficient permissions | "Permissions insuffisantes" |
| MISSION_FULL | Mission at capacity | "Cette mission est complète" |
| MISSION_CANCELLED | Mission was cancelled | "Cette mission a été annulée" |
| INVALID_TRANSITION | Status transition not allowed | "Changement de statut non autorisé" |
| DUPLICATE_SIGNUP | Already signed up | "Vous êtes déjà inscrit(e) à cette mission" |
