plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    "implementation"(libs.dagger.lib)
    "ksp"(libs.dagger.compiler)
}

/*
pluginManager.withPlugin("app.cash.sqldelight") {
    // Search for module which uses an android gradle plugin by looking at extensions
    extensions
        .extensionsSchema
        .elements
        // Filter by extensions which inherit from `AndroidComponentsExtension` like
        // `ApplicationAndroidComponentsExtension` or `LibraryAndroidComponentsExtension`
        .filter { typeOf(AndroidComponentsExtension::class.java).isAssignableFrom(it.publicType) }
        .map { extensions.getByType(it.publicType) as AndroidComponentsExtension<*, *, *> }
        .forEach {
            // Let's configure the android's extension
            it.onVariants(it.selector().all()) { variant ->
                afterEvaluate {
                    // Ksp must run after SQLDelight has generated its classes. Inspired by
                    // https://github.com/google/dagger/issues/4158
                    val variantName = variant.name.capitalized()
                    tasks.getByName<KotlinCompile>("ksp${variantName}Kotlin")
                        .setSource(tasks.getByName("generate${variantName}DatabaseInterface").outputs)
                }
            }
        }
}*/
