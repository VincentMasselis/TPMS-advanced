import org.gradle.api.attributes.java.TargetJvmEnvironment.STANDARD_JVM
import org.gradle.api.attributes.java.TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE

plugins {
    id("app.cash.paparazzi")
}

// Related issue https://github.com/cashapp/paparazzi/issues/1025
dependencies.constraints {
    add("testImplementation", "com.google.guava:guava") {
        attributes {
            attribute(
                TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                objects.named<TargetJvmEnvironment>(STANDARD_JVM)
            )
        }
        because(
            "LayoutLib and sdk-common depend on Guava's -jre published variant." +
                    "See https://github.com/cashapp/paparazzi/issues/906."
        )
    }
}