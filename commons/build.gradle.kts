plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.formatter) apply false
}

allprojects {
    group = "com.sv.youapp.common"
    version = rootProject.libs.versions.global.get()
}


subprojects {
    pluginManager.apply(rootProject.libs.plugins.formatter.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.publish.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.kotlin.spring.get().pluginId)
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
}
