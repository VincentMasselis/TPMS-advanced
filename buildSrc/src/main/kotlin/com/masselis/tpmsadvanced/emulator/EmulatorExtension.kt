package com.masselis.tpmsadvanced.emulator

import org.gradle.api.provider.Property

public abstract class EmulatorExtension {
    public abstract val emulatorPackage: Property<String>
    public abstract val emulatorName: Property<String>
}
