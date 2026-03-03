# Quickstart: Civic Missions MVP

## Development Setup

### Prerequisites

- Kotlin 2.2.0
- JDK 17+
- Android Studio or IntelliJ IDEA
- Xcode (for iOS, macOS only)

### Build Commands

```bash
# Build all targets
./gradlew build

# Run server only
./gradlew :server:run

# Run web app (WASM)
./gradlew :composeApp:wasmJsBrowserRun

# Run Android (requires Android SDK)
./gradlew :composeApp:androidInstallDebug

# Run iOS (requires Xcode)
./gradlew :composeApp:iosSimulatorRun
```

### Running Tests

```bash
# Unit tests
./gradlew :shared:test

# All tests
./gradlew test
```

---

## Project Structure Overview

```
ShakeYourTown/
├── composeApp/       # UI layer (Compose Multiplatform)
│   └── src/
│       └── commonMain/
│           └── kotlin/
│               └── com/shakeyourtown/
│                   └── missions/     # Feature module
├── shared/           # Business logic layer
│   └── src/
│       └── commonMain/
│           └── kotlin/
│               └── com/shakeyourtown/
│                   └── missions/     # Domain models, use cases
├── server/           # Backend layer (Ktor)
│   └── src/
│       └── kotlin/
│           └── com/shakeyourtown/
│               └── missions/     # API routes, services
└── specs/            # Feature specifications
    └── 001-civic-missions/
```

---

## Key Files

### Backend Entry Point
- `server/src/kotlin/com/shakeyourtown/Application.kt`

### API Routes
- `server/src/kotlin/com/shakeyourtown/missions/MissionsRoutes.kt`
- `server/src/kotlin/com/shakeyourtown/missions/SignupRoutes.kt`
- `server/src/kotlin/com/shakeyourtown/missions/AuthRoutes.kt`

### Data Storage
- `server/src/kotlin/com/shakeyourtown/missions/storage/MissionStorage.kt`

### Shared Models
- `shared/src/commonMain/kotlin/com/shakeyourtown/missions/models/`

### UI Components
- `composeApp/src/commonMain/kotlin/com/shakeyourtown/missions/ui/`

---

## Configuration

### Server Port
Default: `8080`
Configurable via environment variable `PORT`

### Data Directory
Default: `./server/data`
Configurable via environment variable `DATA_DIR`

### First-time Setup

1. Run server with initial data:
   ```bash
   # Creates default organizer account
   # Username: admin
   # Password: changeme123
   ```

2. Access admin panel at: `http://localhost:8080/admin`

---

## Testing Checklist

- [ ] Server starts without errors
- [ ] Public mission list loads
- [ ] Mission filters work (theme, availability)
- [ ] Citizen can sign up without account
- [ ] Signup confirmation displayed
- [ ] Organizer can log in
- [ ] Organizer can create/edit mission
- [ ] Organizer can publish mission
- [ ] Mission appears on public board
- [ ] Organizer can view signups
- [ ] Organizer can export CSV
- [ ] Organizer can update status
- [ ] Archive shows completed missions

---

## Performance Targets

Per Constitution Principle IV:
- UI response: <100ms
- Cold start: <3s
- Data sync: <5s
- Memory: <150MB

---

## Troubleshooting

### Server won't start
- Check port 8080 is available
- Verify JDK 17+ is installed: `java -version`

### Build fails
- Clean and rebuild: `./gradlew clean build`
- Check Gradle wrapper: `./gradlew wrapper`

### iOS build issues
- Verify Xcode is installed: `xcodebuild -version`
- Accept licenses: `sudo xcodebuild -license`
