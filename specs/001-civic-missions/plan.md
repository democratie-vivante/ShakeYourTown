# Implementation Plan: Civic Missions MVP

**Branch**: `001-civic-missions` | **Date**: 2026-03-03 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-civic-missions/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Build a civic missions MVP enabling a small municipality (~3,800 residents) to publish short, practical missions (soft mobility, resource sharing, food autonomy) that citizens can discover and sign up for in under 1 minute without account creation. Features public mission board with filters, organizer back-office for mission management, and a public archive of completed missions to build trust through visible progress.

## Technical Context

**Language/Version**: Kotlin 2.2.0  
**Primary Dependencies**: Kotlin stdlib, Compose Multiplatform 1.8.2, Ktor 3.2.0 (minimal - no external dependencies)  
**Storage**: File-based JSON with in-memory caching (see research.md)  
**Testing**: Kotlin Test, JUnit 4.13.2, Compose UI Testing  
**Target Platform**: Android (minSdk 24), iOS, Web (WASM), Server (Ktor)  
**Project Type**: Kotlin Multiplatform mobile-app + web-service  
**Performance Goals**: UI response <100ms, cold start <3s, data sync <5s, memory <150MB (per constitution)  
**Constraints**: ~3,800 residents, offline-capable desirable for rural connectivity  
**Scale/Scope**: 50 concurrent visitors, 380 expected monthly participants, ~20-50 missions/year

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Code Quality Standards | ✅ PASS | Kotlin conventions, functions under 30 lines, code review required |
| II. Test-Driven Development | ✅ PASS | TDD cycle, 80% coverage target, integration tests for cross-platform sync |
| III. Unified User Experience | ✅ PASS | Compose Multiplatform ensures consistent UI across platforms |
| IV. Performance Requirements | ✅ PASS | Targets aligned: <100ms UI, <3s cold start, <5s sync, <150MB |
| V. Shared Code First | ✅ PASS | Business logic in shared module, platform-specific code limited |
| VI. API Contract Stability | ✅ PASS | Versioned API (/api/v1/), manual OpenAPI for MVP |
| VII. Data Privacy by Design | ✅ PASS | File-based storage supports export/deletion, minimum data collected |

**Phase 1 Complete**: All gates passing

## Project Structure

### Documentation (this feature)

```text
specs/001-civic-missions/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
composeApp/              # Kotlin Multiplatform Compose UI
├── src/
│   ├── commonMain/      # Shared UI, ViewModels, navigation
│   ├── androidMain/
│   ├── iosMain/
│   ├── desktopMain/
│   └── wasmJsMain/

shared/                   # Shared business logic, models
├── src/
│   └── commonMain/      # Domain models, repository interfaces, use cases

server/                   # Ktor backend
├── src/
│   ├── Main.kt         # Server entry point
│   ├── routing/        # API routes
│   ├── services/       # Business logic
│   └── storage/       # Data persistence (file-based for MVP)

tests/                    # Test sources
├── unit/                # Unit tests
└── integration/         # Integration tests
```

**Structure Decision**: KMP project with existing structure. Backend in /server, UI in /composeApp, shared logic in /shared. Feature modules will be added within existing directories.

## Complexity Tracking

No complexity violations requiring justification. All constitution principles satisfied with standard KMP patterns.
