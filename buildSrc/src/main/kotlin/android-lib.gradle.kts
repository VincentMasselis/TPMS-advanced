plugins {
    com.android.library
    `kotlin-parcelize`
    id("android-common")
}

android {
    defaultConfig {
        consumerProguardFile("consumer-rules.pro")
    }
}
