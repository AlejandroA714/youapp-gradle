plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.5" apply false
    id("com.google.cloud.tools.jib") version "3.4.0" apply false
    id("com.sv.youapp.infrastructure.formatter") version "1.0.0-SNAPSHOT" apply false
}

allprojects {
    group = "com.sv.youapp.services"
    version = "1.0.3-SNAPSHOT"
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "com.google.cloud.tools.jib")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "com.sv.youapp.infrastructure.formatter")
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }
    dependencies{
        implementation(platform("com.sv.youapp.infrastructure:bom:1.0.0-SNAPSHOT"))
        implementation("org.springframework.boot:spring-boot-starter-web"){
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        }
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.boot:spring-boot-starter-undertow")
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
            image = "alejandroa714/zulu-jvm:21.0.8-jre-headless"
            platforms {
                platform { architecture = "arm64"; os = "linux" }
                platform { architecture = "amd64"; os = "linux" }
            }
        }
        to {
            image = "alejandroa714/${project.name}"
            tags = setOf(project.version.toString())
            auth {
                username = System.getenv("DOCKER_USERNAME")
                password = System.getenv("DOCKER_PASSWORD")
            }
        }
        container {
            jvmFlags = listOf("-XX:MaxRAMPercentage=75","-server","-Djava.security.egd=file:/dev/./urandom")
            creationTime = "USE_CURRENT_TIMESTAMP"
            volumes = listOf("/tmp")
            //user = "valejo"
            workingDirectory = "/app"
        }
    }
}
