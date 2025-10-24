plugins {
    alias(libs.plugins.java.library)
    alias(libs.plugins.formatter) apply false
}

allprojects {
    group = "com.sv.youapp.common"
    version = rootProject.libs.versions.global.get()
}


subprojects {
    pluginManager.apply(rootProject.libs.plugins.java.library.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.formatter.get().pluginId)
    pluginManager.apply(rootProject.libs.plugins.publish.get().pluginId)
    dependencies{
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
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
