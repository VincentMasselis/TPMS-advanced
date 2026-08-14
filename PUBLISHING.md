## Publish in beta

Git-flow is enforced by the `gitflow` buildSrc plugin, backed by the `git` CLI. Every step below is
a plain Gradle task - the `workflow_dispatch` entry points just run the same tasks in CI.

From `develop`, run each as its own `./gradlew` invocation (required: `bumpVersion` and
`writeReleaseNote` write the version and the release note to disk, and each following task reads
the version catalog fresh - combining them in one command line would use stale values):

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

Pushing the release branch triggers
[beta.yml](https://github.com/VincentMasselis/TPMS-advanced/actions/workflows/beta.yml):
`assertReleaseBranchIsValid` (checks the branch was cut from `develop`, its version isn't already
tagged/branched, and `main` is fully merged into `develop`), `build` + `verifyPaparazzi`,
`createGithubPreRelease`, then `publishToPlayStoreBetaNormalRelease`.

Hotfixes use the same workflows/tasks from `main` with `flow=hotfix` (pick `bump=patch` yourself -
nothing enforces it), cutting `hotfix/<version>`. Pushing it triggers
[hotfix.yml](https://github.com/VincentMasselis/TPMS-advanced/actions/workflows/hotfix.yml), which
only runs `assertHotfixBranchIsValid`, `build` and `verifyPaparazzi` - no beta publish or
pre-release; a hotfix only reaches users once merged into `main`.

## Publish in production

Pushing to `main` (via merging a `hotfix/*` or `release/*` branch) triggers
[production.yml](https://github.com/VincentMasselis/TPMS-advanced/actions/workflows/production.yml):
`assertVersionWasNotPushInProductionYet`, `createGithubRelease`,
`publishToPlayStoreProductionNormalRelease`, `updatePlayStoreScreenshotsNormalRelease`.

A second job then opens (or reuses) a merge-commit PR back into `develop` with auto-merge enabled,
keeping `develop` from drifting behind `main`.
