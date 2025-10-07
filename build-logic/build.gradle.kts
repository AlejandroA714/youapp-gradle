plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.java.gradle)
}

dependencies {
    implementation(libs.spotless)
}

kotlin {
    jvmToolchain(21)
}
group = "com.sv.youapp.infrastructure"
version = libs.versions.global.get()

gradlePlugin {
    plugins {
        create("formatterPlugin") {
            id = "$group.formatter"
            implementationClass = "com.sv.youapp.infrastructure.formatter.FormatterPlugin"
            displayName = "YouApp Formatter Plugin"
            description = "Aplica y configura Spotless (ktlint, googleJavaFormat, etc.)"
        }
    }
}
