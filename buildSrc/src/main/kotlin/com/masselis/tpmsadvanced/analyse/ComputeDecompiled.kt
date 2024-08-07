package com.masselis.tpmsadvanced.analyse

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal abstract class ComputeDecompiled : DefaultTask() {

    @get:Input
    abstract val service: Property<DecompilerService>

    @TaskAction
    fun proceed() {
        service.get().decompiled
    }
}