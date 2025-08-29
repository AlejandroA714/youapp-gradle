rootProject.name = "platform-instances"

include(":authorization-server")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}
