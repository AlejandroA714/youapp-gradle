rootProject.name = "platform-instances"

include(":authentication-server")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}
