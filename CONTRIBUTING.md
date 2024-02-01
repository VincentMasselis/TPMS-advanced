## Technical requirements

* [Kotlin](https://kotlinlang.org/)
  with [coroutine & flow](https://kotlinlang.org/docs/coroutines-overview.html)
* Multi-module based on [this approach](https://developer.android.com/topic/modularization)
* Clean Architecture (Entities, Use-Cases, Interfaces)
* [Compose](https://developer.android.com/jetpack/compose)
* IOC with [Dagger2](https://dagger.dev/)
* Gradle with [Android Gradle Plugin](https://developer.android.com/studio/build)

## How to build

Run `git@github.com:VincentMasselis/TPMS-advanced.git` on your machine then `./gradlew build`. You
don't need the secrets keys to run this project, the gradle configuration works without them.

## Release in beta

The preferred way to release is by pushing a commit into the `main` branch. That will
trigger [this workflow](.github/workflows/beta.yml) which calls the gradle
tasks `createGithubReleaseNormalRelease` and  `publishToPlayStoreBetaNormalRelease`.

- `createGithubReleaseNormalRelease` Tags the current commit with the version code and it will
  create a beta github release
- `publishToPlayStoreBetaNormalRelease` Create a signed and minified aab, sent it automatically to
  the play store and publish it into the __beta channel__.

## Promote in production

To promote this release to the __production channel__ you must push a tag with the
string `production` on the specified commit. This calls the
task `promoteGithubReleaseNormalRelease`, `updatePlayStoreScreenshotsNormalRelease`
and `promoteToPlayStoreProductionNormalRelease`.

- `promoteGithubReleaseNormalRelease` Updates the github release listed to the "production" status.
- `updatePlayStoreScreenshotsNormalRelease` Takes screenshots of the app and push them to the play
  store listings.
- `promoteToPlayStoreProductionNormalRelease` Promotes the release from the beta channel to the
  __production__.
