# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Lint (Detekt)
./gradlew detekt

# Unit tests for a specific module
./gradlew :<module>:testDebugUnitTest --tests "com.example.ClassName#methodName"

# Compose screenshot tests
./gradlew verifyPaparazzi       # verify
./gradlew recordPaparazzi       # record new snapshots

# Instrumented tests (requires emulator/device)
./gradlew pixel2api34DemoDebugAndroidTest

# Full CI-equivalent run
./gradlew build verifyPaparazzi pixel2api34DemoDebugAndroidTest
```

## Architecture

Multi-module Android app (Kotlin, Jetpack Compose, API 27–36) organized into three layers:

- **`core/`** — cross-cutting infrastructure: `common` (utilities), `database` (SQLDelight driver), `ui` (Compose/Material3/Navigation), `test` / `android-test` (shared test helpers)
- **`data/`** — domain models and persistence: `vehicle` (tyre/sensor/vehicle SQLDelight DB), `unit` (pressure/temperature conversions), `app` (app-level config)
- **`feature/`** — UI features: `main` (BLE scanning + tyre monitoring), `background` (background service), `qrcode` (MLKit barcode), `unlocated`, `shortcut`, `unit`, `android-auto`
- **`app/phone/`** — application entry point, wires everything together, Paparazzi tests live here

## Key Patterns

### Dependency Injection — Metro
The project uses [Metro](https://github.com/ZacSweers/metro) (not Dagger/Hilt). The convention per module is:

```kotlin
// Public surface — what other modules consume
interface FeatureComponent {
    val someUseCase: SomeUseCase
    companion object : FeatureComponent by InternalComponent
}

// Actual wiring — internal to the module
@DependencyGraph(AppScope::class, bindingContainers = [Bindings::class])
internal interface InternalComponent : FeatureComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes parent: ParentComponent): InternalComponent
    }
    companion object : InternalComponent by createGraphFactory<Factory>().build(...)
}
```

### Database — SQLDelight
Room is not used. SQLDelight generates type-safe Kotlin from `.sq` files. Migrations use `.sqm` files alongside `.db` snapshot files in `data/vehicle/src/main/sqldelight/`. Every new migration must be accompanied by an updated schema snapshot.

### Build Flavors
Two flavors: `demo` (for Play Store screenshots/testing) and `normal` (production).

### Convention Plugins
Reusable Gradle config lives in `buildSrc/src/main/kotlin/` as convention plugins (`android-app`, `android-lib`, `compose`, `detekt`, `gitflow`, `monitor-resource`). Apply these to new modules rather than duplicating config.

### Versioning
A custom `GitflowPlugin` manages semantic versioning automatically from the git-flow branch structure. Releases are cut via `./gradlew createRelease` on the `develop` branch.
