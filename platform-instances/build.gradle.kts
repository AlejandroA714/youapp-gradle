import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

plugins {
    alias(libs.plugins.java.api)
    alias(libs.plugins.spring.boot)  apply false
    alias(libs.plugins.tools.jib) apply false
    alias(libs.plugins.formatter) apply false
}

allprojects {
    group = "com.sv.youapp.service"
    version = rootProject.libs.versions.global.get()
}

subprojects {
    pluginManager.apply(rootProject.libs.plugins.java.api.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.formatter.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.publish.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.spring.boot.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.tools.jib.get().pluginId)
    dependencies{
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
        implementation(rootProject.libs.spring.boot.starter.validation)
        implementation(platform(rootProject.libs.bom))
    }
    configurations.configureEach {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
       // exclude(group = "org.springframework.boot", module = "spring-boot-starter-reactor-netty")
    }

    val outputDir = layout.buildDirectory.dir("generated/resources")
    val generateLocalIpProps = tasks.register("generateLocalIpProps") {
        outputs.dir(outputDir)
        doLast {
            val ip = resolveNonLoopbackAddress()
            val file = outputDir.get().file("application-local.properties").asFile
            file.parentFile.mkdirs()
            file.writeText(
                """
                    LOCAL_IP=$ip
                    """.trimIndent() + "\n"
            )
            println("[${project.name}] Generated local-ip.properties with app.local-ip=$ip")
        }
    }
    val sourceSets = extensions.getByType<SourceSetContainer>()
    sourceSets.named("main") {
        resources.srcDir(outputDir)
    }
    tasks.named("processResources") {
        dependsOn(generateLocalIpProps)
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


fun resolveNonLoopbackAddress(): String {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val ni = interfaces.nextElement()
            if (!ni.isUp || ni.isLoopback || ni.isVirtual) continue

            val addrs = ni.inetAddresses
            while (addrs.hasMoreElements()) {
                val addr = addrs.nextElement()
                if (!addr.isLoopbackAddress
                    && addr is Inet4Address
                    && addr.isSiteLocalAddress
                ) {
                    return addr.hostAddress
                }
            }
        }
        "127.0.0.1"
    } catch (e: SocketException) {
        "127.0.0.1"
    }
}

fun timeStamp(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss"))
}
