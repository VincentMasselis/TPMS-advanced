## Technical requirements

* [Kotlin](https://kotlinlang.org/)
  with [coroutine & flow](https://kotlinlang.org/docs/coroutines-overview.html)
* Multi-module based on [this approach](https://developer.android.com/topic/modularization)
* Clean Architecture (Entities, Use-Cases, Interfaces)
* [Compose](https://developer.android.com/jetpack/compose)
* IOC with [Dagger2](https://dagger.dev/)
* Gradle with [Android Gradle Plugin](https://developer.android.com/studio/build)
* Gradle KTS with custom plugins
* Git-flow

## How to build

Run `git@github.com:VincentMasselis/TPMS-advanced.git` on your machine then `./gradlew build`. You
don't need the secrets keys to run this project, the gradle configuration works without them.

## Publish in beta

TPMS-Advanced follow the rules of git-flow. To create a release from `develop`, call the task
`createRelease`. This task will create a release with a version which match the one filled into the
root [build.gradle file](build.gradle.kts). Push this release to github to run the tasks:

- `assertReleaseBranchIsValid`: Checks the release branch is valid according to the git-flow
  branching model
- `build` and `verifyPaparazzi`: Build the app and run unit tests
- `createGithubPreRelease`: Create a github pre-release with release notes and attached apks
- `publishToPlayStoreBetaNormalRelease`: Sends the aabs to the play store into the beta track with
  the corresponding release note

## Publish in production

To publish into the production track, you have to push a commit on the `main` branch. The commit
could comme from a `hotfix/*` branch or a `release/*` branch. Push this release to github to run the
tasks:

- `assertMainCommitNewVersion`: Ensure the version to upload is a new version
- `createGithubRelease`: Create a github release with release notes and attached apks
- `publishToPlayStoreProductionNormalRelease`: Sends the aabs to the play store into the production
  track with the corresponding release note
- `updatePlayStoreScreenshotsNormalRelease`: Update the listing's screenshot to match the latest
  app release
