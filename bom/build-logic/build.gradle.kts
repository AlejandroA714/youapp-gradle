plugins {
    kotlin("jvm") version "2.2.0"
    `java-gradle-plugin`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}

kotlin {
    jvmToolchain(21)
}
group = "com.sv.youapp.infrastructure"
version = "1.0.3-SNAPSHOT"

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