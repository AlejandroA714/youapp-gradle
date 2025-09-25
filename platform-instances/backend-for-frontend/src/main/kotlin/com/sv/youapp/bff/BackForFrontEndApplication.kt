package com.sv.youapp.bff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(    exclude = [
    org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration::class
])
class BackForFrontEndApplication

fun main(args: Array<String>) {
    runApplication<BackForFrontEndApplication>(*args)
}
