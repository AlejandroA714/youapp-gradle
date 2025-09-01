plugins {
    kotlin("plugin.jpa") version "2.2.0"
}
dependencies {
    implementation(kotlin("reflect"))
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
}
