public val fetchAllTags: Task = task<Exec>("fetchAllTags") {
    commandLine("git", "fetch")
    args("--all", "--tags")
}

public val gitTagVersion: Task = task<Exec>("gitTagVersion") {
    dependsOn(fetchAllTags)
    val tpmsAdvancedVersionCode: Int by rootProject.extra
    commandLine("git", "tag", "vc${tpmsAdvancedVersionCode}")
}

public val gitPushTag: Task = task<Exec>("gitPushTag") {
    dependsOn(gitTagVersion)
    commandLine("git", "push")
    args("--tags")
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.github.triplet.play"))
            tasks["publishApps"].dependsOn(gitPushTag)
    }
}