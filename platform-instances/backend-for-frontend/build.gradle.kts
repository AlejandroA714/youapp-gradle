dependencies{
    implementation(libs.kotlin.reflect)
    implementation(rootProject.libs.bundles.web.flux)
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")
    implementation("jakarta.validation:jakarta.validation-api")
    testImplementation("io.projectreactor:reactor-test")
}
