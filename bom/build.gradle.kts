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
    api(platform (libs.junit.bom))
    api(platform(libs.spring.boot.dependencies))
    constraints {
        api(libs.lombok)
        api(libs.mysql.connector)
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
