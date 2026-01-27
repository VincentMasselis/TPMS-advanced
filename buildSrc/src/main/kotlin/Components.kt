import com.android.build.api.variant.AndroidComponentsExtension

internal fun AndroidComponentsExtension<*, *, *>.ignoreDemoRelease() = beforeVariants {
    if (it.productFlavors.any { (_, flavor) -> flavor == "demo" } && it.buildType == "release")
        it.enable = false
}