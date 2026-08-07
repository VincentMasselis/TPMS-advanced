## Publish in beta

TPMS-Advanced follows the rules of git-flow, enforced by the `gitflow` buildSrc plugin
(`buildSrc/src/main/kotlin/com/masselis/tpmsadvanced/gitflow/`). It's backed entirely by the `git`
CLI, and every action below is a plain Gradle task you can run locally - the `workflow_dispatch`
entry point below just runs the same tasks in CI.

Before cutting a release, add the Play Store release note for the new version under
`app/phone/src/normal/play/release-notes/en-US/<version>.txt` - `createRelease`/`createHotfix`
check it exists before doing anything else.

To create a release from `develop`, run:

```shell
./gradlew createRelease pushGitflowBranch -Pgitflow.bump=minor  # or major / patch
```

`createRelease` cuts `release/<version>` from `develop`'s current tip and bumps
`gradle/libs.versions.toml`'s `app` version on the new branch (never on `develop` - that's what
keeps the automatic main-to-develop back-merge conflict-free, see below). `pushGitflowBranch` can
also be run on its own afterward if you'd rather review the bump commit before pushing.

Alternatively, use the **Gitflow** workflow's `workflow_dispatch` trigger from the Actions tab (or
`gh workflow run gitflow.yml -f flow=release -f bump=minor -f release-note="..."`) to do this from
CI directly - supply the release note text as a workflow input instead of committing the file
yourself.

Pushing the release branch triggers `beta.yml`, which runs:

- `assertReleaseBranchIsValid`: checks the release branch was actually cut from `develop` (not
  `main` or a feature branch), that its version isn't already tagged or branched elsewhere, and
  that `main` is fully merged into `develop`
- `build` and `verifyPaparazzi`: build the app and run unit tests
- `createGithubPreRelease`: create a GitHub pre-release with release notes and attached APKs
- `publishToPlayStoreBetaNormalRelease`: send the AABs to the Play Store beta track

Hotfixes work the same way from `main`, always bumping patch (`./gradlew createHotfix
pushGitflowBranch` takes no `-Pgitflow.bump`, or use `workflow_dispatch` with `flow=hotfix`);
pushing `hotfix/<version>` triggers `hotfix.yml`.

## Publish in production

To publish into the production track, push a commit on the `main` branch - it comes from merging a
`hotfix/*` or `release/*` branch. Push this to GitHub to run `production.yml`, which runs:

- `assertVersionWasNotPushInProductionYet`: ensure the version to upload is a new version
- `createGithubRelease`: create a GitHub release with release notes and attached APKs
- `publishToPlayStoreProductionNormalRelease`: send the AABs to the Play Store production track
- `updatePlayStoreScreenshotsNormalRelease`: update the listing's screenshots

Once that's done, a second job automatically opens (or reuses) a pull request merging `main` back
into `develop` and enables GitHub's auto-merge with a merge commit (never squash/rebase, so both
branches stay alive) - this is what keeps `develop` from silently drifting behind `main`.
