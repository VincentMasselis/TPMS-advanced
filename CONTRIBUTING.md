## Technical requirements

* [Kotlin](https://kotlinlang.org/)
  with [coroutine & flow](https://kotlinlang.org/docs/coroutines-overview.html)
* Multi-module based on [this approach](https://developer.android.com/topic/modularization)
* Clean Architecture (Entities, Use-Cases, Interfaces)
* [Compose](https://developer.android.com/jetpack/compose)
* IOC with [Metro](https://zacsweers.github.io/metro/latest/index.html)
* Gradle with [Android Gradle Plugin](https://developer.android.com/studio/build)
* Gradle KTS with custom plugins
* Git-flow

## How to build

Run `git@github.com:VincentMasselis/TPMS-advanced.git` on your machine then `./gradlew build`. You
don't need the secrets keys to run this project, the Gradle configuration works without them.

### Secrets (optional)

Release signing, Play Store publishing, Firebase, and GitHub release notes need secrets that are
fetched at build time from a Bitwarden/Vaultwarden vault, via the `bw` CLI (install with
`npm install -g @bitwarden/cli@2026.7.0`). Add the following keys to your gitignored
`local.properties`:

```properties
bitwarden.item=<bitwarden-item>
# Can be "bitwarden.com", "bitwarden.eu" or a custom url
bitwarden.server=<bitwarden-servel-url>
bitwarden.email=<bitwarden-account-email>
bitwarden.password=<associated-account-password>
```

Environment variables `BITWARDEN_ITEM`, `BITWARDEN_SERVER`, `BITWARDEN_EMAIL` and
`BITWARDEN_PASSWORD` can be used instead of `local.properties` if needed.

## Publishing

See [PUBLISHING.md](PUBLISHING.md) for the git-flow release/hotfix process.
