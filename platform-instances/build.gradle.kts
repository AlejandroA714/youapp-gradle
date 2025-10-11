import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot)  apply false
    alias(libs.plugins.tools.jib) apply false
    alias(libs.plugins.formatter) apply false
}

allprojects {
    group = "com.sv.youapp.service"
    version = rootProject.libs.versions.global
}

subprojects {
    pluginManager.apply(rootProject.libs.plugins.formatter.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.publish.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.kotlin.spring.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.spring.boot.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.tools.jib.get().pluginId)
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }
    dependencies{
        implementation(platform(rootProject.libs.bom))
    }

    extensions.configure<PublishingExtension>("publishing") {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }

    extensions.configure(com.google.cloud.tools.jib.gradle.JibExtension::class.java) {
        from {
            image = rootProject.libs.versions.image.get()
            platforms {
                platform { architecture = "arm64"; os = "linux" }
                platform { architecture = "amd64"; os = "linux" }
            }
        }
        to {
            setAllowInsecureRegistries(true)
            image = "alejandroa714/${project.name}"
            tags = setOf("${project.version}.${timeStamp()}")
            auth {
                username = System.getenv("DOCKER_USERNAME")
                password = System.getenv("DOCKER_PASSWORD")
            }
        }
        container {
            jvmFlags = listOf("-XX:MaxRAMPercentage=75","-server","-Djava.security.egd=file:/dev/./urandom","-Djava.awt.headless=true")
            creationTime = "USE_CURRENT_TIMESTAMP"
            volumes = listOf("/tmp")
            user = "spring"
            workingDirectory = "/app"
        }
    }
}

fun timeStamp(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss"))
}
