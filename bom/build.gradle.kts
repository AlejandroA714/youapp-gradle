plugins {
    `java-platform`
    `maven-publish`
}

javaPlatform {
    allowDependencies()
}

allprojects{
    group = "com.sv.youapp.infrastructure"
    version = "1.0.0-SNAPSHOT"
    apply(plugin = "maven-publish")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencies {
    api(platform(libs.spring.boot.dependencies))
    constraints {
        api(libs.spring.authorization)
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
            artifactId = "bom"
        }
    }
}