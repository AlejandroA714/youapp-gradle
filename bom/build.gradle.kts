plugins {
    alias(libs.plugins.platform)
    alias(libs.plugins.publish)
}

group = "com.sv.youapp.infrastructure"
version = libs.versions.global.get()

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(libs.spring.boot.dependencies))
    api(platform (libs.junit.bom))
    constraints {
        api(libs.spring.authorization.server)
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
            artifactId = project.name
        }
    }
}
