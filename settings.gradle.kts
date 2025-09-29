rootProject.name = "backend"

includeBuild("bom")
includeBuild("platform-instances")

//dependencyResolutionManagement {
//    versionCatalogs {
//        create("libs") {
//            from(files("gradle/libs.versions.toml")) // O usa "bom/gradle/libs.versions.toml"
//        }
//    }
//}