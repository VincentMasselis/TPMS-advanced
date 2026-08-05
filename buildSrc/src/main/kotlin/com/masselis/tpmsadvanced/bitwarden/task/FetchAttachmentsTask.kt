package com.masselis.tpmsadvanced.bitwarden.task

import com.masselis.tpmsadvanced.bitwarden.worker.FetchAttachment
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkerExecutor
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull


internal abstract class FetchAttachmentsTask : DefaultTask() {

    @get:Internal
    abstract val session: Property<Optional<String>>

    @get:Internal
    abstract val itemName: Property<String>

    @get:Input
    abstract val files: MapProperty<String, RegularFile>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @TaskAction
    fun execute() {
        val session = session.get().getOrNull() ?: run {
            logger.warn("Cannot download secrets, credentials are missing or incorrect, skipping the task")
            return
        }

        workerExecutor.noIsolation().apply {
            files.get().forEach { (name, location) ->
                submit(FetchAttachment::class) {
                    this.session = session
                    this.itemName = this@FetchAttachmentsTask.itemName
                        .orNull
                        ?.ifBlank { null }
                        ?: throw GradleException("\"itemName\" property is missing")
                    this.fileName = name
                    this.outputFile = location
                }
            }
            await()
        }
    }
}