plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    "implementation"(libs.koin.core)
    // Visibility set to "api" because Dagger analyses each annotation for each type used in the
    // `dependency` field for `@Component`
    "api"(libs.koin.annotation)
    "ksp"(libs.koin.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
