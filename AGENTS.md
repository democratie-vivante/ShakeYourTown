# ShakeYourTown Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-03-06

## Active Technologies

- Kotlin 2.2.20 + Kotlin stdlib, Compose Multiplatform 1.10.1, Ktor Client 3.3.1 (HTTP only)
- No backend server - zero-cost MVP architecture
- Data storage: browser localStorage (WASM), in-memory (Android/iOS)
- Publishing: GitHub Pages (static JSON)
- Signups: Google Forms

## Architecture (Zero-Cost MVP)

```text
Organizer device (browser / app)
  └─ Creates/edits missions locally (localStorage)
  └─ Publishes to GitHub Pages via GitHub Contents API
         │
Citizen browser (WASM web app)
  └─ Fetches missions from static JSON URL
  └─ Submits signups to Google Forms
         │
Google Sheets (organizer reads signups there)
```

## Project Structure

```text
composeApp/
  src/commonMain/kotlin/
    io/mbras/syt/App.kt                          # Navigation coordinator
    com/shakeyourtown/missions/
      ui/                                         # All UI screens + ViewModels
      storage/                                    # Local storage (PlatformStorage, MissionStorage, SettingsStorage)
      github/                                     # GitHub Pages publisher
  src/wasmJsMain/                                 # WASM (web) entry point + localStorage impl
  src/androidMain/                                # Android entry point + storage impl
  src/iosMain/                                    # iOS entry point + storage impl
shared/
  src/commonMain/kotlin/
    com/shakeyourtown/missions/models/            # Mission, Signup, Organizer data models
```

## Commands

```bash
# Run WASM dev server (primary target)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Build WASM production
./gradlew :composeApp:wasmJsBrowserDistribution

# Run Android
./gradlew :composeApp:installDebug
```

## Code Style

Kotlin 2.2.20: Follow standard conventions

## Localization

- **Target audience**: French municipality (~3,800 inhabitants)
- **Primary language**: French (fr-FR)
- All user-facing content (UI labels, messages, error messages, notifications) MUST be in French
- Internal code identifiers, API field names, and enum values remain in English
- Date format: dd/MM/yyyy, decimal separator: comma

## Recent Changes

- feat/zero-cost-mvp: Removed backend server, switched to localStorage + GitHub Pages + Google Forms
- 001-civic-missions: Original implementation with Ktor server backend

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
