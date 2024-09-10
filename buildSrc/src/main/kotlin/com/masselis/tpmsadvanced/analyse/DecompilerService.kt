package com.masselis.tpmsadvanced.analyse

import com.masselis.tpmsadvanced.analyse.DecompilerService.Parameter
import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

internal abstract class DecompilerService : BuildService<Parameter>, AutoCloseable {

    interface Parameter : BuildServiceParameters {
        val apkToDecompile: RegularFileProperty
    }

    private val lazyDecompilation = lazy {
        JadxArgs()
            .apply { setInputFile(parameters.apkToDecompile.get().asFile) }
            .let { JadxDecompiler(it) }
            .apply { load() }
    }
    val decompiled by lazyDecompilation

    override fun close() {
        if (lazyDecompilation.isInitialized())
            decompiled.close()
    }
}
