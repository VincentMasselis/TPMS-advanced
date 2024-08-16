package com.masselis.tpmsadvanced.analyse

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

internal abstract class ComputeDecompiled : DefaultTask() {

    @get:Internal
    abstract val service: Property<DecompilerService>

    @TaskAction
    fun proceed() {
        service.get().decompiled
    }
}