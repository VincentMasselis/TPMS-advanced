plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    base(this)
    defaultConfig {
        consumerProguardFile("consumer-rules.pro")
    }
}
