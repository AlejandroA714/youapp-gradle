dependencies {
    implementation(libs.spring.core.oauth2)
    implementation(libs.spring.boot.starter.redis)
    implementation(libs.spring.authorization.server)
    implementation("com.sv.youapp.common:authorization-commons")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
