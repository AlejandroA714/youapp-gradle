dependencies {
    implementation(rootProject.libs.bundles.web.flux) {
        exclude("org.springframework.boot", "spring-boot-starter-reactor-netty")
    }
}
