# ShakeYourTown Constitution

## Core Principles

### I. Code Quality Standards
Every feature MUST maintain high code quality through: consistent Kotlin coding conventions across all targets (Android, iOS, Web, Server), meaningful naming for all identifiers, single-responsibility functions under 30 lines, and comprehensive documentation for public APIs. Code MUST be reviewed by at least one other contributor before merging. Technical debt MUST be tracked and addressed in subsequent iterations.

**Rationale**: Kotlin Multiplatform requires consistent practices across platforms; maintainability depends on readable, well-structured code.

### II. Test-Driven Development (NON-NEGOTIABLE)
All new functionality MUST be developed using TDD: unit tests written first (RED), implementation satisfies tests (GREEN), then refactor (REFACTOR). Minimum 80% code coverage required for business logic. Every public function MUST have at least one corresponding unit test. Integration tests REQUIRED for: cross-platform data sync, API contracts, and shared state management.

**Rationale**: TDD ensures correctness from the start and provides regression protection as the codebase grows.

### III. Unified User Experience
All platform UIs (Android, iOS, Web) MUST present consistent: terminology and labeling, interaction patterns, visual hierarchy, and accessibility standards. Platform-specific UI components MAY be used only when necessary for native feel, but MUST adhere to the shared design system. Every screen MUST be tested on all target platforms before release.

**Rationale**: Users expect consistent experience regardless of how they access the app; trust in the democratic process requires professional presentation.

### IV. Performance Requirements
The application MUST meet these performance targets: UI response time under 100ms for user interactions, cold start under 3 seconds on mobile, data sync completion under 5 seconds for typical operations, and memory usage under 150MB on mobile devices. Performance MUST be measured before each release using profiling tools.

**Rationale**: Citizens using the app expect responsive experience; poor performance discourages participation in democratic processes.

### V. Shared Code First
Common business logic, data models, and domain rules MUST reside in the shared module. Platform-specific code is LIMITED to: native UI components, platform integrations (notifications, biometrics), and performance-critical rendering. Any duplication across platforms MUST be refactored into shared code.

**Rationale**: Kotlin Multiplatform's value proposition is code sharing; duplication undermines maintainability and consistency.

### VI. API Contract Stability
All API endpoints (server and internal) MUST use explicit versioned contracts. Breaking changes REQUIRE a new version number and migration path. API documentation MUST be auto-generated from code contracts. Integration tests MUST verify contract compliance.

**Rationale**: The app serves a municipality; stability ensures reliable access for all citizens regardless of their device or app version.

### VII. Data Privacy by Design
The application MUST collect only minimum personal data necessary for functionality. User data MUST be: encrypted at rest and in transit, retained only for required duration, and exportable/deletable by users. Privacy policy MUST be accessible from every data collection point.

**Rationale**: Civic apps handle sensitive citizen data; trust requires demonstrable privacy protection.

## Additional Constraints

### Technology Stack
- **Language**: Kotlin 1.9+ (multiplatform)
- **UI Framework**: Compose Multiplatform
- **Backend**: Ktor server
- **Database**: [TO BE DETERMINED per feature]
- **Testing**: Kotlin Test, Compose UI Testing

### Accessibility Requirements
All UI MUST meet WCAG 2.1 AA standards. Screen reader compatibility REQUIRED for all interactive elements. Touch targets minimum 48dp on mobile. Color contrast ratios MUST meet 4.5:1 for normal text.

### Localization Requirements
**Primary language**: French (fr-FR). All user-facing content (UI labels, messages, confirmations, error messages, notifications, privacy notices) MUST be in French. Internal technical identifiers (API field names, enum values, code identifiers) remain in English. Date and number formatting MUST follow French conventions (dd/MM/yyyy, comma as decimal separator).

### Security Standards
All API endpoints MUST require authentication where applicable. Input validation on all user-provided data. No secrets stored in code or client-side storage. HTTPS-only for all network communication.

## Development Workflow

### Quality Gates (Required for Merge)
- All unit tests pass
- All integration tests pass
- Code coverage unchanged or improved
- No linting/formatting violations
- Documentation updated for any new public APIs
- Performance benchmarks meet targets

### Code Review Requirements
- Minimum 1 reviewer approval for all changes
- Reviewer MUST verify: tests included, no unintended regressions, documentation updated
- Complex changes require 2 reviewers

### Release Process
- Release candidates tagged with semantic versioning
- Release notes generated automatically from changelog
- Hotfixes follow same process as regular releases (fast-track allowed)

## Governance

**Amendment Procedure**: Any principle change requires: proposed amendment documented in PR, rationale provided with alternatives considered, migration plan if breaking, and approval from project maintainers.

**Versioning Policy**: This constitution follows semantic versioning. MAJOR: Backward incompatible principle changes. MINOR: New principles or expanded guidance. PATCH: Clarifications, wording improvements.

**Compliance Review**: Every feature specification MUST verify alignment with constitution principles. Every implementation plan MUST include a Constitution Check section.

**Version**: 1.0.0 | **Ratified**: 2026-03-03 | **Last Amended**: 2026-03-03
