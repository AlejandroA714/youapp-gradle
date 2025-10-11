plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(rootProject.libs.bundles.web) {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation("com.zaxxer:HikariCP")
    implementation("org.hibernate.orm:hibernate-core")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.data:spring-data-redis")
    implementation("redis.clients:jedis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
    implementation("com.sv.youapp.common:authorization-commons")
    runtimeOnly("com.mysql:mysql-connector-j")
}
