package com.masselis.tpmsadvanced.analyse

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal abstract class PrintClasses : DefaultTask() {

    @get:Input
    abstract val service: Property<DecompilerService>

    init {
        group = "verification"
    }

    @TaskAction
    fun process() = service.get().decompiled.classes.take(50).forEach {
        println(it.name)
    }
}