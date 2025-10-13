plugins {
    `android-lib`
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.core.database"
}

dependencies {
    implementation(project(":core:common"))

    api(libs.sqldelight.android.driver)
    api(libs.sqldelight.coroutines.ext)
    api(libs.sqldelight.coroutines.jvm)
    api(libs.sqldelight.primitive.adapters)

    // By default, sqldelight uses the sqlite engine bundled into the android framework which could
    // be an old version of the sqlite engine not supported by the app. This dependency include the
    // latest sqlite engine used to replace the old one.
    // Requery used as implementation to force using SQLiteOpenHelperUseCase instead of using
    // directly RequerySQLiteOpenHelperFactory
    implementation(libs.requery)
    implementation(libs.relinker)
}