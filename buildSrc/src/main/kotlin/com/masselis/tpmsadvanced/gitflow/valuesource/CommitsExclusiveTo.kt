package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.CommitsExclusiveTo.Parameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * The commits reachable from [Parameters.include] but from none of [Parameters.excludes], as a
 * set of SHAs. Backed by `git rev-list <include> ^<exclude> ^<exclude> ...`.
 *
 * Used both to compute the release/hotfix branch's own commits (relative to develop/main) and, by
 * calling it again for every other pushed branch, to detect when those "own" commits are also
 * reachable from a branch other than the expected parent - the "foreign commit" branch-provenance
 * check in `rule/`.
 */
internal abstract class CommitsExclusiveTo : ValueSource<Set<String>, Parameters> {

    internal interface Parameters : ValueSourceParameters {
        val include: Property<String>
        val excludes: ListProperty<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): Set<String> = ByteArrayOutputStream()
        .also { stdout ->
            execOperations.exec {
                commandLine(listOf("git", "rev-list", parameters.include.get()) + parameters.excludes.get().map { "^$it" })
                standardOutput = stdout
            }
        }
        .use { it.toString() }
        .lineSequence()
        .map(String::trim)
        .filter(String::isNotBlank)
        .toSet()
}
