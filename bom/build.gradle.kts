plugins {
    alias(libs.plugins.java.platform)
    alias(libs.plugins.publish)
}

group = "com.sv.youapp.infrastructure"
version = libs.versions.global.get()

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(libs.junit.bom))
    api(platform(libs.logbook.bom))
    api(platform(libs.spring.boot.dependencies))
    api(platform(libs.spring.cloud.dependencies))
    constraints {
        api(libs.lombok)
        api(libs.caffeine.cache)
        api(libs.mysql.connector)
        api(libs.spring.authorization.server)
        api(libs.spring.cloud.starter.gateway)
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
