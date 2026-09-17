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
./gradlew pixel2api34DebugAndroidTest

# Full CI-equivalent run
./gradlew build verifyPaparazzi pixel2api34DebugAndroidTest
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

### Demo Mode
No build flavors — a single build. Demo mode (used for Play Store screenshots/testing) is a runtime setting toggled from the app's settings screen, backed by `ScannerDatabase.isDemo` in `data/vehicle`.

### Convention Plugins
Reusable Gradle config lives in `buildSrc/src/main/kotlin/` as convention plugins (`android-app`, `android-lib`, `compose`, `detekt`, `gitflow`, `monitor-resource`). Apply these to new modules rather than duplicating config.

### Versioning
A custom `GitflowPlugin` (git-cli backed, no JGit) manages semantic versioning automatically from the git-flow branch structure and validates the branch model (release must fork from `develop`, hotfix from `main`, no duplicate tags/branches, no foreign commits). Releases are cut via `./gradlew createRelease -Pgitflow.bump=major|minor|patch` on `develop` (`./gradlew createHotfix` from `main`, always patch), followed by `./gradlew pushGitflowBranch` — or via the `Gitflow` GitHub Actions `workflow_dispatch`, which runs the same tasks. A push to `main` also auto-opens a merge-commit back-merge PR into `develop`. See [CONTRIBUTING.md](CONTRIBUTING.md) for the full flow.

## Code Style

Chaining is the default control flow in this codebase, not an occasional idiom. Favor it over imperative statement sequences with intermediate named `val`s.

- **Chain, don't name.** Prefer scope functions (`let`, `also`, `run`; `apply`/`with` are rarer here) over declaring a `val` and using it a few lines later. Each intermediate value should live only inside the scope that needs it, then disappear. Chains 3-5 calls deep are normal, including for side-effecting code:
  ```kotlin
  database.selectAll()
      .execute()
      .firstOrNull { it.uuid != vehicle.uuid }
      ?.also { currentVehicleUseCase.setAsCurrent(it) }
      ?: error("Cannot delete the last vehicle in the database")
  ```
  Guard clauses / early returns don't need to be forced into a scope-function shape.
- **Extension functions are the chain-enabling tool.** When an API isn't chain-friendly, wrap it in a small extension function/property on its receiver instead of a free helper function, so the call site reads as a verb in the chain (e.g. `file.asToml()`, `Query.kt`'s `.asList()`/`.asOne()`, `Pressure.kt`'s `Float.kpa`/`.bar`). Put these in a file named after the receiver, never a generic `Extensions.kt`.
- **Extraction bar: 2+ call sites, or a real conceptual boundary.** Don't extract a `private fun` (or local `fun`) that's only called from a single call site — inline that logic into the caller instead. Exception: in long Compose screens, splitting a giant composable into several single-call-site `private fun` helpers (one per list section/dialog/etc.) is fine when it measurably helps readability — the goal is minimizing reader context-switching, not a hard rule.
- **Explicit visibility always.** Write `public`/`internal`/`private` explicitly, even on top-level declarations — never rely on the default.
- **Modeling**: value classes (`@JvmInline`) for zero-cost domain primitives, data classes for plain records, sealed interfaces/classes for state modeling (not for error channels). `val` almost exclusively; `var` only for local Compose state or explicit mutable accumulators.
- **Errors**: prefer `error(...)`, `require(...)`, `requireNotNull`/`checkNotNull` (with a lambda message when useful) over custom exception types or `Result`/`Either` wrappers. Recover from stream-level failures with `.catch { emit(FallbackState) }`.
- **Null handling**: elvis + scope functions instead of `if` (`x?.let { ... } ?: ...`), `takeIf` to turn sentinel values into `null`. Don't shy away from nullable types in domain APIs.
- **Boolean negation**: never use the prefix `!` operator to invert a condition; use a trailing `.not()` instead (e.g. `condition.not()` rather than `!condition`).
- **Comments**: feel free to comment more than the rest of the codebase does when it helps explain what generated code is doing — the user will trim what turns out to be unnecessary. When writing code meant to match the existing human-authored style long-term, keep comments purposeful (workarounds, why an exception is swallowed, non-obvious API contracts) rather than restating what the code does.
- **File organization**: one primary public type per file, matching the filename. Nest small, tightly-coupled types inside the type that owns them rather than splitting into sibling files.
- **Formatting**: 4-space indent, leading-dot chain wrapping (one call per line), fully-expanded imports (including importing enum constants directly so they can be used bare in `when`). Prefer `@Suppress` (`MaxLineLength`, `LongMethod`, etc.) over force-fragmenting cohesive logic to satisfy linters.
- **Compose**: public composables take a trailing `modifier: Modifier = Modifier` and often a `viewModel: XViewModel = viewModel { ... }` default for testability. Split a public stateful composable from a private stateless one that takes the resolved state, to support previews with mock state. Colocate `@Preview` functions (one per state variant) in the same file.
- **Tests**: JUnit4 + MockK + Turbine. `@Before fun setup()` plus a private `test()` factory built from fields set in `setup()`. Backtick natural-language names for scenario/behavior tests, camelCase for simple state-transition tests. Prefer `assertIs<State.X>(...)` over instanceof-style checks on sealed state.
