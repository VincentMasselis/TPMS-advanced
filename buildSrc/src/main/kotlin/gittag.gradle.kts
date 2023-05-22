val fetchAllTags = task<Exec>("fetchAllTags"){
    commandLine("git", "fetch")
    args("--all", "--tags")
}

val gitTagVersion = task<Exec>("gitTagVersion")   {
    dependsOn(fetchAllTags)
    val tpmsAdvancedVersionCode: Int by rootProject.extra
    commandLine("git", "tag", "vc${tpmsAdvancedVersionCode}")
}

val gitPushTag = task<Exec>("gitPushTag") {
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