plugins {
    id("android-lib")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.database"
}

dependencies {
    val sqlDelightVersion: String by project
    api("app.cash.sqldelight:android-driver:$sqlDelightVersion")
    api("app.cash.sqldelight:coroutines-extensions-jvm:$sqlDelightVersion")
    api("app.cash.sqldelight:primitive-adapters:$sqlDelightVersion")
    // By default, sqldelight uses the sqlite engine bundled into the android framework which could
    // be an old version of the sqlite engine not supported by the app. This dependency include the
    // latest sqlite engine used to replace the old one.
    api("com.github.requery:sqlite-android:3.39.2")
}