package com.masselis.tpmsadvanced.analyse

import gradle.kotlin.dsl.accessors._c0ebf38a9e0e766e40379ba7eaa32ea4.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import java.io.File
import java.util.Optional

public class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val ext = extensions.create<LibraryExtension>("obfuscationAnalyserModule", path)
        ext.minimalObfuscationPercentageOpt.convention(Optional.empty())
        ext.packageWatchList.addAll(provider {
            kotlin
                .sourceSets
                .matching {
                    when {
                        it.name.startsWith("androidTest") -> false
                        it.name.startsWith("test") -> false
                        else -> true
                    }
                }
                .fold(mutableSetOf<File>()) { acc, sourceSet -> acc += sourceSet.kotlin.files; acc }
                .mapNotNullTo(mutableSetOf()) { file ->
                    file.useLines { lines ->
                        lines
                            // ⚠️ Kotlin files at root folder don't have the package line
                            .firstOrNull { it.startsWith("package") }
                            // Same syntax for Kotlin or JAVA except for the ';' character in the end
                            ?.substringAfter("package ")
                            // In case of JAVA file
                            ?.substringBefore(';')
                    }
                }
                .map { "^${it.replace(".", "\\.")}$".toRegex() }
        })

        /*
        // In case of pure JAVA modules
        android
            .sourceSets
            .matching {
                val names = it.toString().split(' ')
                when {
                    names[2] == "test" -> false
                    names[2] == "android" && names.getOrNull(3) == "test" -> false
                    else -> true
                }
            }
            .configureEach {
                println(this)
                // Prints every java file
                java.getSourceFiles().files.forEach { println(it) }
            }*/
    }
}
