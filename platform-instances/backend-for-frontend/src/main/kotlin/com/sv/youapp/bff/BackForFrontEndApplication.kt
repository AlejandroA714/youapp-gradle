package com.sv.youapp.bff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(    exclude = [
    ReactiveUserDetailsServiceAutoConfiguration::class
])
class BackForFrontEndApplication

fun main(args: Array<String>) {
    runApplication<BackForFrontEndApplication>(*args)
}
