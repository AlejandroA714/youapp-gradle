rootProject.name = "components"

include(":native-grant-component")
include(":redis-handling-session")
include(":jpa-handling-session")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    includeBuild("../build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
