rootProject.name = "platform-instances"

include(":api-gateway")
include(":authorization-server")
include(":backend-for-frontend")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    includeBuild("../bom/build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
