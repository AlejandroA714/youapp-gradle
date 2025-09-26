rootProject.name = "platform-instances"

include(":api-gateway")
include(":authorization-server")
include(":backend-for-frontend")

pluginManagement {
    includeBuild("../bom/build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
