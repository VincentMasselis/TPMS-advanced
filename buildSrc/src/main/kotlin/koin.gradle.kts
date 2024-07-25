plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    "implementation"(libs.koin.core)
    "implementation"(libs.koin.annotation)
    "ksp"(libs.koin.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
}
