package com.masselis.tpmsadvanced.github

import com.masselis.tpmsadvanced.github.task.UpsertRelease
import com.masselis.tpmsadvanced.github.task.ForceTagCommit
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create

public class GithubPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<GithubExtension>("github")
        val tagName = ext.versionName.map { it.toString() }

        val forceTagCommit = project.tasks.create<ForceTagCommit>("forceTagCommit") {
            tag = tagName
        }
        project.tasks.create<UpsertRelease>("upsertGithubPreRelease") {
            dependsOn(forceTagCommit)
            githubToken = ext.githubToken
            this.tagName = tagName
            preRelease = true
            preReleaseBranch = ext.preReleaseBranch
            releaseBranch = ext.releaseBranch
        }
        project.tasks.create<UpsertRelease>("upsertGithubRelease") {
            dependsOn(forceTagCommit)
            githubToken = ext.githubToken
            this.tagName = tagName
            preRelease = false
            preReleaseBranch = ext.preReleaseBranch
            releaseBranch = ext.releaseBranch
        }
    }
}