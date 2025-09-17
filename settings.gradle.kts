rootProject.name = "platform-instances"

include("api-gateway")
include(":authorization-server")
include(":backend-for-frontend")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}
