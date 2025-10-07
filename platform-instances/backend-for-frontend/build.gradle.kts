dependencies{
    implementation(rootProject.libs.bundles.web.flux) {
        exclude("org.springframework.boot", "spring-boot-starter-reactor-netty")
    }
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")
    testImplementation("io.projectreactor:reactor-test")
}
