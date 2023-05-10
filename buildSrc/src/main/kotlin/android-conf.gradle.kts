afterEvaluate {
    if (plugins.hasPlugin("com.android.library"))
        apply(plugin = "android-lib")
    if (plugins.hasPlugin("com.android.application"))
        apply(plugin = "android-app")
}