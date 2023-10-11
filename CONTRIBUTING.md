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

## Release

The preferred way to release is by pushing a commit into the `main` branch. That will
trigger [this workflow](.github/workflows/main.yml) which calls the gradle
task `tagCommitAndPublishToBetaNormalRelease`. `tagCommitAndPublishToBetaNormalRelease` creates a
signed and minified aab, sent it automatically to the play store and publish it into the __beta
channel__.

To promote this release to the __production channel__ you must push a tag with the
string `production` on the specified commit. This calls `promoteToMainNormalRelease` and it will
automatically updates the screenshots on the Play Store listing.
