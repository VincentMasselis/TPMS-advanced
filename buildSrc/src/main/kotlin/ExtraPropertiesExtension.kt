import org.gradle.api.plugins.ExtraPropertiesExtension

public inline fun <reified T> ExtraPropertiesExtension.getOrNull(name: String): T? =
    if (has(name)) get(name) as T else null