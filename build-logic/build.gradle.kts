plugins {
    alias(libs.plugins.java.gradle)
}

dependencies {
    implementation(libs.spotless)
}

group = "com.sv.youapp.infrastructure"
version = libs.versions.global.get()

gradlePlugin {
    plugins {
        create("formatterPlugin") {
            id = "$group.formatter"
            implementationClass = "com.sv.youapp.infrastructure.formatter.FormatterPlugin"
            displayName = "YouApp Formatter Plugin"
            description = "Aplica y configura Spotless (googleJavaFormat, etc.)"
        }
    }
}
