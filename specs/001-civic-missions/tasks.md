# Tasks: Civic Missions MVP

**Feature**: 001-civic-missions | **Generated**: 2026-03-03 | **Plan**: [plan.md](plan.md)

## Implementation Strategy

This implementation follows an MVP-first approach with incremental delivery:
- **MVP Scope**: User Story 1 (P1) - Citizen signup flow + minimal backend
- **Phase 1**: Setup and infrastructure
- **Phase 2**: Foundational components (shared models, storage, auth)
- **Phases 3-7**: User stories in priority order, each independently testable
- **Phase 8**: Polish and cross-cutting concerns

## Phase 1: Setup

Project initialization and configuration.

- [X] T001 Create missions feature directory structure in server/src/kotlin/io/mbras/syt/missions/
- [X] T002 Create missions feature directory structure in shared/src/commonMain/kotlin/com/shakeyourtown/missions/
- [X] T003 Create missions feature directory structure in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/
- [X] T004 Add Ktor server dependencies to server/build.gradle.kts (ktor-server-core, ktor-server-netty)
- [X] T005 Configure server port and data directory in server/src/kotlin/io/mbras/syt/Application.kt

## Phase 2: Foundational

Shared infrastructure required for all user stories.

- [X] T006 [P] Define Mission data class in shared/src/commonMain/kotlin/com/shakeyourtown/missions/models/Mission.kt
- [X] T007 [P] Define Signup data class in shared/src/commonMain/kotlin/com/shakeyourtown/missions/models/Signup.kt
- [X] T008 [P] Define Organizer data class in shared/src/commonMain/kotlin/com/shakeyourtown/missions/models/Organizer.kt
- [X] T009 [P] Define MissionTheme enum in shared/src/commonMain/kotlin/com/shakeyourtown/missions/models/MissionTheme.kt
- [X] T010 [P] Define MissionStatus enum in shared/src/commonMain/kotlin/com/shakeyourtown/missions/models/MissionStatus.kt
- [X] T011 Create MissionStorage service in server/src/main/kotlin/io/mbras/syt/missions/storage/MissionStorage.kt
- [X] T012 Create SignupStorage service in server/src/main/kotlin/io/mbras/syt/missions/storage/SignupStorage.kt
- [X] T013 Create OrganizerStorage service in server/src/main/kotlin/io/mbras/syt/missions/storage/OrganizerStorage.kt
- [X] T014 Create authentication middleware in server/src/main/kotlin/io/mbras/syt/missions/auth/AuthMiddleware.kt
- [X] T015 Create session management in server/src/main/kotlin/io/mbras/syt/missions/auth/SessionManager.kt

## Phase 3: User Story 1 - Citizen Discovers and Signs Up (P1)

**Goal**: Allow citizens to browse missions and sign up without creating an account.

**Independent Test**: User visits public page, filters missions, views details, submits signup - all without authentication.

- [X] T016 [P] [US1] Implement GET /api/v1/missions endpoint in server/src/main/kotlin/io/mbras/syt/missions/routes/MissionsRoutes.kt
- [X] T017 [P] [US1] Implement theme filtering (MOBILITY, RESOURCES, FOOD) in MissionsRoutes.kt
- [X] T018 [P] [US1] Implement availability filtering (upcoming only vs all) in MissionsRoutes.kt
- [X] T019 [US1] Implement GET /api/v1/missions/{id} endpoint for mission details in MissionsRoutes.kt
- [X] T020 [US1] Implement POST /api/v1/missions/{id}/signup endpoint in server/src/main/kotlin/io/mbras/syt/missions/routes/MissionsRoutes.kt
- [X] T021 [US1] Add validation: at least email OR phone required, in MissionsRoutes.kt
- [X] T022 [US1] Add validation: cannot sign up if mission FULL/CANCELLED/DONE, in MissionsRoutes.kt
- [X] T023 [US1] Add validation: cannot sign up twice with same contact, in MissionsRoutes.kt
- [X] T024 [US1] Display privacy notice at signup (FR-005) in composeApp UI
- [X] T025 [US1] Create MissionListScreen in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/MissionListScreen.kt
- [X] T026 [US1] Create MissionDetailScreen in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/MissionDetailScreen.kt
- [X] T027 [US1] Create SignupForm component in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/SignupFormScreen.kt
- [X] T028 [US1] Implement MissionListViewModel in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/MissionListViewModel.kt
- [X] T029 [US1] Add theme filter UI (Mobility/Resources/Food buttons) in MissionListScreen.kt

## Phase 4: User Story 2 - Organizer Creates and Publishes Mission (P1)

**Goal**: Enable organizers to create missions with all details and publish them.

**Independent Test**: Organizer logs in, creates draft mission, fills required fields, publishes it - verified on public board.

- [X] T030 [P] [US2] Implement POST /api/v1/auth/login endpoint in server/src/kotlin/com/shakeyourtown/missions/routes/AuthRoutes.kt
- [X] T031 [P] [US2] Implement POST /api/v1/auth/logout endpoint in AuthRoutes.kt
- [X] T032 [US2] Implement POST /api/v1/organizer/missions (create) in server/src/kotlin/com/shakeyourtown/missions/routes/OrganizerMissionsRoutes.kt
- [X] T033 [US2] Implement PUT /api/v1/organizer/missions/{id} (update) in OrganizerMissionsRoutes.kt
- [X] T034 [US2] Implement PATCH /api/v1/organizer/missions/{id}/status in OrganizerMissionsRoutes.kt
- [X] T035 [US2] Add validation: status transitions follow allowed transitions per data-model.md
- [X] T036 [US2] Create organizer login screen in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/OrganizerLoginScreen.kt
- [X] T037 [US2] Create mission editor form in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/MissionEditorScreen.kt
- [X] T038 [US2] Create organizer dashboard in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/OrganizerDashboardScreen.kt
- [X] T039 [US2] Implement OrganizerViewModel in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/OrganizerViewModel.kt

## Phase 5: User Story 3 - Citizen Browses Public Archive (P2)

**Goal**: Show completed missions to build trust through visible proof of action.

**Independent Test**: Any visitor accesses archive, views past completed missions with outcomes.

- [ ] T040 [P] [US3] Implement GET /api/v1/missions/archive endpoint in server/src/kotlin/com/shakeyourtown/missions/routes/MissionsRoutes.kt
- [ ] T041 [P] [US3] Implement period filter (month, quarter, year) in MissionsRoutes.kt
- [ ] T042 [US3] Create ArchiveScreen in composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/ArchiveScreen.kt
- [ ] T043 [US3] Display actualParticipants and completionSummary for completed missions in ArchiveScreen.kt
- [ ] T044 [US3] Add date period filter UI (This Month/Quarter/Year/All) in ArchiveScreen.kt

## Phase 6: User Story 4 - Organizer Manages Signups (P2)

**Goal**: Allow organizers to view signups, export data, update mission status.

**Independent Test**: Organizer views signup list, exports CSV, changes status to Done/Cancelled.

- [ ] T045 [P] [US4] Implement GET /api/v1/organizer/missions/{id}/signups in server/src/kotlin/com/shakeyourtown/missions/routes/OrganizerMissionsRoutes.kt
- [ ] T046 [P] [US4] Implement GET /api/v1/organizer/missions/{id}/export (CSV) in OrganizerMissionsRoutes.kt
- [ ] T047 [US4] Add automatic FULL status when currentParticipants reaches maxParticipants
- [ ] T048 [US4] Display signup list in OrganizerDashboardScreen.kt
- [ ] T049 [US4] Add CSV export button in OrganizerDashboardScreen.kt
- [ ] T050 [US4] Add status update buttons (Publish/Cancel/Done) in OrganizerDashboardScreen.kt

## Phase 7: User Story 5 - Organizer Communicates Updates (P3)

**Goal**: Enable organizers to send updates to participants.

**Independent Test**: Organizer sends update, participants receive notification via email.

- [ ] T051 [P] [US5] Create notification service in server/src/kotlin/com/shakeyourtown/missions/services/NotificationService.kt
- [ ] T052 [US5] Implement automatic notification on mission cancellation
- [ ] T053 [US5] Implement automatic notification on mission status change
- [ ] T054 [US5] Add email sending capability via configurable SMTP in NotificationService.kt

## Phase 8: Polish & Cross-Cutting Concerns

Final integration, testing, and refinement.

- [ ] T055 Verify all public endpoints return correct status codes per contracts/api-v1.md
- [ ] T056 Add error handling for invalid mission IDs (404)
- [ ] T057 Add error handling for validation failures (400 with details)
- [ ] T058 Create default organizer account on first server startup
- [ ] T059 Add loading states to all UI screens
- [ ] T060 Add empty state messages when no missions available
- [ ] T061 Verify SC-001: Signup flow completes in under 1 minute
- [ ] T062 Verify SC-002: Published missions visible within 30 seconds
- [ ] T063 Test signup on web, android, and ios targets

## Dependencies

```
Phase 1 (Setup)
    │
    ▼
Phase 2 (Foundational) ◄──────────────────┐
    │                                       │
    ▼                                       │
Phase 3 (US1 - Citizen Signup)             │
    │                                       │
    ▼                                       │
Phase 4 (US2 - Organizer Create) ──────────┤
    │                                       │
    ▼                                       │
Phase 5 (US3 - Archive) ───────────────────┤
    │                                       │
    ▼                                       │
Phase 6 (US4 - Manage Signups) ────────────┤
    │                                       │
    ▼                                       │
Phase 7 (US5 - Notifications) ────────────┘
    │
    ▼
Phase 8 (Polish)
```

## Parallel Execution

The following tasks can be executed in parallel (different files, no dependencies):

- **Phase 2**: T006-T010 (Model definitions) - All independent
- **Phase 3**: T016-T019 (Public endpoints) - All independent
- **Phase 3**: T024-T027 (UI components) - All independent  
- **Phase 4**: T030-T031 (Auth endpoints) - Both independent
- **Phase 4**: T036-T038 (Auth UI) - All independent
- **Phase 5**: T040-T042 (Archive) - All independent
- **Phase 6**: T045-T046 (Signup management) - Both independent
- **Phase 7**: T051-T054 (Notifications) - All independent

## MVP Scope (Recommended First Iteration)

For fastest time-to-value, implement only:

- Phase 1: Setup (T001-T005)
- Phase 2: Foundational (T006-T015)
- Phase 3: User Story 1 - Citizen Signup (T016-T029)

This delivers the core value: citizens can browse and sign up for missions. Organizer functionality can be added in subsequent iteration.

## Summary

| Metric | Value |
|--------|-------|
| Total Tasks | 63 |
| Setup Phase | 5 |
| Foundational Phase | 10 |
| User Story 1 (P1) | 14 |
| User Story 2 (P1) | 10 |
| User Story 3 (P2) | 5 |
| User Story 4 (P2) | 6 |
| User Story 5 (P3) | 4 |
| Polish Phase | 9 |
| Parallelizable Tasks | 20 |
| MVP Tasks | 29 |
