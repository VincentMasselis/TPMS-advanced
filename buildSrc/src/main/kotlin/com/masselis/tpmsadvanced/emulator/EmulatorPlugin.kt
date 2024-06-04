package com.masselis.tpmsadvanced.emulator

import com.masselis.tpmsadvanced.emulator.task.CreateEmulator
import com.masselis.tpmsadvanced.emulator.task.InstallImage
import com.masselis.tpmsadvanced.emulator.task.StartEmulator
import com.masselis.tpmsadvanced.emulator.task.WaitForEmulator
import com.masselis.tpmsadvanced.emulator.task.AcceptLicense
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.task
import java.io.File
import java.util.Properties

public class EmulatorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (project !== project.rootProject)
            throw GradleException("This plugin must be applied to the root project because it can only manage a single emulator at time")

        val ext = project.extensions.create<EmulatorExtension>("emulator").apply {
            emulatorName = "android_emulator"
        }

        val sdkPath = project.file(
            System.getenv("ANDROID_HOME")
                ?: Properties()
                    .apply { load(project.file("local.properties").bufferedReader()) }
                    .getOrElse("sdk.dir") {
                        throw GradleException("Cannot find android SDK location")
                    }
                    .toString()
        )

        val acceptLicense = project.task<AcceptLicense>("acceptSdkManagerLicense") {
            this.sdkPath = sdkPath
        }

        val installImage = project.task<InstallImage>("installAndroidImage") {
            dependsOn(acceptLicense)
            this.sdkPath = sdkPath
            this.emulatorPackage = ext.emulatorPackage
        }

        val createEmulator = project.task<CreateEmulator>("createAndroidEmulator") {
            dependsOn(installImage)
            this.sdkPath = sdkPath
            this.emulatorPackage = ext.emulatorPackage
            this.emulatorName = ext.emulatorName
        }

        val outputDir = project.mkdir("build/emulator")

        val startEmulator = project.task<StartEmulator>("startAndroidEmulator") {
            dependsOn(createEmulator)
            this.sdkPath = sdkPath
            this.emulatorName = ext.emulatorName
            standardOutput = project.file("$outputDir/standard_output.txt").createIfNotExists()
            errorOutput = project.file("$outputDir/error_output.txt").createIfNotExists()
        }

        project.task<WaitForEmulator>("waitForEmulator") {
            dependsOn(startEmulator)
            this.sdkPath = sdkPath
            this.emulatorName = ext.emulatorName
        }
    }

    private fun File.createIfNotExists(): File {
        if (exists().not())
            createNewFile()
        return this
    }
}