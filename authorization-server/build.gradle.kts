plugins {
    kotlin("plugin.jpa") version "2.2.0"
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-webflux")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-jetty")
    }
}

dependencies {
    implementation(kotlin("reflect"))
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
}
