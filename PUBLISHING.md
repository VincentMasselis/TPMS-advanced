## Publish in beta

Git-flow is enforced by the `gitflow` buildSrc plugin, backed by the `git` CLI. Every step below is
a plain Gradle task - the `workflow_dispatch` entry points just run the same tasks in CI.

Before cutting a release, add the Play Store release note for the new version under
`app/phone/src/main/play/release-notes/en-US/<version>.txt` - `createRelease`/`createHotfix`
check it exists before doing anything else.

To create a release from `develop`, run:

```shell
./gradlew bumpVersion -Pversion.bump=minor            # or major / patch
./gradlew writeReleaseNote -Pversion.releaseNote="..."
./gradlew createRelease commitAddedFiles pushGitflowBranch \
  -Pgitflow.gitflowBranchCommitMessage="Version bump"
```

`writeReleaseNote` only exists if `secrets/publisher-service-account.json` is present locally (see
[CONTRIBUTING.md](CONTRIBUTING.md)); always available in CI.

Alternatively, trigger the
[Start a new version branch](https://github.com/VincentMasselis/TPMS-advanced/actions/workflows/gitflow-start-version.yml)
workflow (`gh workflow run gitflow-start-version.yml -f flow=release -f bump=minor -f
release-note="..."`) to do this from CI. Its `release-note` input is a single-line text box - use
literal `\n` for line breaks (e.g. `- Fix A\n- Fix B`); the workflow unescapes it before writing
the file.

If the version bump and release note are already committed to `develop` yourself, skip to
`createRelease commitAddedFiles pushGitflowBranch`, or trigger
[Cut a version branch](https://github.com/VincentMasselis/TPMS-advanced/actions/workflows/gitflow-cut-branch.yml)
(`gh workflow run gitflow-cut-branch.yml -f flow=release`).

- `assertReleaseBranchIsValid`: checks the release branch was actually cut from `develop` (not
  `main` or a feature branch), that its version isn't already tagged or branched elsewhere, and
  that `main` is fully merged into `develop`
- `build` and `verifyPaparazzi`: build the app and run unit tests
- `createGithubPreRelease`: create a GitHub pre-release with release notes and attached APKs
- `publishToPlayStoreBetaRelease`: send the AAB to the Play Store beta track

`publishToPlayStoreBetaRelease` and `publishToPlayStoreProductionRelease` (below) both read
`-PplayStore.changesNotSentForReview`, wired to the `PLAY_STORE_CHANGES_NOT_SENT_FOR_REVIEW` repo
variable in CI. Leave it `false` normally; set it to `true` after a Play Store rejection, since the
console then forces this flag until someone manually re-submits the app for review from the
console.

Hotfixes use the same workflows/tasks from `main` with `flow=hotfix` (pick `bump=patch` yourself -
nothing enforces it), cutting `hotfix/<version>`. Pushing it triggers
[hotfix.yml](https://github.com/VincentMasselis/TPMS-advanced/actions/workflows/hotfix.yml), which
runs the buildSrc unit tests, `assertHotfixBranchIsValid`, `build`, `verifyPaparazzi` and the
instrumented tests (`pixel2api34DebugAndroidTest`, `copyScreenshot`) - no beta publish or
pre-release; a hotfix only reaches users once merged into `main`.

## Publish in production

Pushing to `main` (via merging a `hotfix/*` or `release/*` branch) triggers
[production.yml](https://github.com/VincentMasselis/TPMS-advanced/actions/workflows/production.yml):
`assertVersionWasNotPushInProductionYet`, `createGithubRelease`,
`publishToPlayStoreProductionRelease`, `updatePlayStoreScreenshotsRelease`,
`openBackMergePullRequest`.

- `assertVersionWasNotPushInProductionYet`: ensure the version to upload is a new version
- `createGithubRelease`: create a GitHub release with release notes and attached APKs
- `publishToPlayStoreProductionRelease`: send the AAB to the Play Store production track
- `updatePlayStoreScreenshotsRelease`: update the listing's screenshots
- `openBackMergePullRequest`: open (or reuse) a pull request merging `main` back into `develop` and
  enable GitHub's auto-merge with a merge commit (never squash/rebase, so both branches stay alive)
  - this is what keeps `develop` from silently drifting behind `main`

All four tasks run in the same CI job, one after another.
