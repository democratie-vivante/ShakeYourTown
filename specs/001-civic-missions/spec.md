# Feature Specification: Civic Missions MVP

**Feature Branch**: `001-civic-missions`  
**Created**: 2026-03-03  
**Status**: Draft  
**Input**: User description: "Build a civic missions MVP for a small municipality (~3,800 residents) to turn local priorities (soft mobility, resource sharing, food autonomy) into concrete, trackable actions..."

## Public cible & Langue

**Public cible** : Habitants d'une commune française (~3 800 habitants)  
**Langue principale** : Français (fr-FR)  
**Localisation** : Toutes les interfaces utilisateur (citoyens et organisateurs), messages de confirmation, messages d'erreur, notifications et contenus affichés DOIVENT être en français. Les données internes (clés d'API, enums techniques) restent en anglais.

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Citizen Discovers and Signs Up for a Mission (Priority: P1)

A resident visits the public mission board, browses available missions, finds one that matches their interests and schedule, and signs up in under one minute without creating an account.

**Why this priority**: This is the core value proposition - enabling citizens to quickly discover and participate in local missions. Without this, the platform has no purpose.

**Independent Test**: Can be tested by a user visiting the public page, filtering missions, viewing details, and submitting a signup - all without authentication.

**Acceptance Scenarios**:

1. **Given** the mission board is public, **When** a citizen browses without filtering, **Then** they see all published, available missions sorted by date
2. **Given** the citizen applies a theme filter (mobility/resources/food), **When** they select one category, **Then** only missions matching that theme are displayed
3. **Given** the citizen finds an interesting mission, **When** they click to view details, **Then** they see: title, description, date/time, location (text address), needed participants, what to bring, and organizer's contact method
4. **Given** the citizen decides to participate, **When** they enter their name and contact (email or phone) and confirm, **Then** they receive immediate confirmation and the mission's participant count increases
5. **Given** the mission reaches full capacity, **When** a citizen tries to sign up, **Then** they see "Full" status and cannot sign up

---

### User Story 2 - Organizer Creates and Publishes a Mission (Priority: P1)

A municipality staff member or local association organizer creates a new mission with all required details and publishes it to make it visible to citizens.

**Why this priority**: Without organizers creating missions, there is nothing for citizens to discover. This is the supply side of the platform.

**Independent Test**: Can be tested by an organizer logging in, creating a draft mission, filling all required fields, and publishing it - then verifying it appears on the public board.

**Acceptance Scenarios**:

1. **Given** the organizer is authenticated in the back-office, **When** they start creating a new mission, **Then** they can enter: title, description, theme (mobility/resources/food), date and time, location (text address), maximum participants needed, what participants should bring
2. **Given** the organizer saves a mission as draft, **When** they view it later, **Then** it remains in draft status and is not visible on the public board
3. **Given** the organizer has completed all required fields, **When** they publish the mission, **Then** it becomes visible on the public board with "Published" status
4. **Given** the organizer needs to make changes, **When** they edit a published mission, **Then** participants who already signed up are notified of changes

---

### User Story 3 - Citizen Browses Public Archive of Completed Missions (Priority: P2)

A resident or external visitor views the archive of completed missions to see proof of community action and engagement.

**Why this priority**: Transparency and proof of action builds trust in the platform and encourages more participation.

**Independent Test**: Can be tested by any visitor accessing the archive section and viewing past completed missions with their results.

**Acceptance Scenarios**:

1. **Given** the archive page is public, **When** anyone visits, **Then** they see a list of completed (done) missions
2. **Given** the visitor applies a date filter, **When** they select past month/quarter/year, **Then** only missions completed in that period are shown
3. **Given** the visitor clicks on a completed mission, **Then** they see: original mission details plus actual outcome (actual participants, summary of what was accomplished)
4. **Given** the visitor applies a theme filter, **Then** only completed missions matching that theme are displayed

---

### User Story 4 - Organizer Manages Signups and Updates Mission Status (Priority: P2)

An organizer monitors who has signed up for their missions, exports the participant list if needed, and updates the mission status as it progresses.

**Why this priority**: Organizers need to coordinate participation and track mission progress without administrative overhead.

**Independent Test**: Can be tested by an organizer viewing their mission's signup list, changing status, and exporting data.

**Acceptance Scenarios**:

1. **Given** a mission has signups, **When** the organizer views the mission, **Then** they see a list of participants with their contact information
2. **Given** the organizer needs the data externally, **When** they click export, **Then** they receive a downloadable file (CSV format) with participant names and contact info
3. **Given** the mission date passes and the event occurs, **When** the organizer marks it as "Done", **Then** it moves to the public archive
4. **Given** circumstances require cancellation, **When** the organizer marks it as "Cancelled", **Then** all participants are notified and the mission is marked cancelled publicly

---

### User Story 5 - Organizer Communicates Updates to Participants (Priority: P3)

An organizer sends important updates to participants - confirmations, changes, or cancellations - efficiently.

**Why this priority**: Clear communication ensures participants show up and reduces no-shows.

**Independent Test**: Can be tested by an organizer sending a message and participants receiving it via their provided contact method.

**Acceptance Scenarios**:

1. **Given** a participant has signed up, **When** the organizer sends an update, **Then** the participant receives it via email or SMS to the contact they provided
2. **Given** the mission details change significantly, **When** the organizer saves changes, **Then** participants automatically receive a notification of the change
3. **Given** the mission is cancelled, **When** the organizer updates status to cancelled, **Then** all participants are automatically notified

---

### Edge Cases

- What happens when a participant wants to cancel their own signup?
- How does the system handle multiple organizers for the same mission?
- What happens if the organizer enters a past date for a mission?
- How does the system handle duplicate signups (same person signing up twice)?
- What happens if the mission reaches capacity while someone is in the signup process?

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow citizens to browse all published, non-full missions without authentication
- **FR-002**: System MUST allow filtering missions by theme (mobility, resources, food)
- **FR-003**: System MUST allow filtering missions by availability (upcoming only, show all including past)
- **FR-004**: System MUST allow citizens to sign up for a mission by providing only name and either email OR phone number
- **FR-005**: System MUST display a clear explanation at signup explaining why contact information is collected and how it will be used
- **FR-006**: System MUST immediately confirm successful signup to the participant
- **FR-007**: System MUST allow authenticated organizers to create missions with: title, description, theme, date/time, location (text), max participants, what to bring
- **FR-008**: System MUST allow organizers to save missions as draft (not visible publicly) or publish immediately
- **FR-009**: System MUST allow organizers to edit their own missions at any time
- **FR-010**: System MUST automatically prevent signup when a mission reaches maximum participants
- **FR-011**: System MUST allow organizers to view all signups for their missions with participant contact details
- **FR-012**: System MUST allow organizers to export participant data as CSV
- **FR-013**: System MUST allow organizers to update mission status: draft, published, full, cancelled, done
- **FR-014**: System MUST automatically notify participants when a mission they signed up for is cancelled or significantly modified
- **FR-015**: System MUST display a public archive of completed (done) missions visible to anyone without authentication
- **FR-016**: System MUST show mission status (draft, published, full, cancelled, done) clearly on public listings
- **FR-017**: System MUST require organizer authentication for back-office access
- **FR-018**: System MUST NOT collect more participant data than name and contact method (email or phone)
- **FR-019**: System MUST display all user-facing content (UI labels, messages, confirmations, error messages, notifications, privacy notices) in French (fr-FR). Internal technical identifiers (API field names, enum values, HTTP headers) remain in English.

### Key Entities *(include if feature involves data)*

- **Mission**: Represents a single civic action opportunity. Attributes: title, description, theme, date/time, location, max participants, current participants count, status, what to bring, created by (organizer), created at, updated at
- **Signup**: Represents a citizen's registration for a mission. Attributes: mission reference, participant name, contact method (email or phone), signed up at, status (confirmed, cancelled)
- **Organizer**: Represents a user who creates and manages missions. Attributes: name, organization, authentication credentials
- **MissionArchive**: Read-only projection of completed missions for public viewing. Attributes: all mission fields plus actual participant count and completion summary

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Citizens can complete mission signup in under 1 minute from landing on the site to confirmation
- **SC-002**: 100% of published missions are visible on the public board within 30 seconds of publishing
- **SC-003**: Organizers can create and publish a new mission in under 5 minutes
- **SC-004**: 95% of cancelled mission participants receive notification within 1 hour of cancellation
- **SC-005**: Public archive loads within 3 seconds on standard broadband connection
- **SC-006**: System supports at least 50 concurrent public visitors without performance degradation
- **SC-007**: 90% of signups result in confirmed participation (visible via attendance tracking)
- **SC-008**: At least 10% of municipality population (~380 residents) participates in at least one mission within first 6 months

