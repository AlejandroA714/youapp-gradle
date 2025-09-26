package com.sv.youapp.infrastructure.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiGatewayApplication

fun main(args: Array<String>) {
    runApplication<com.sv.youapp.infrastructure.api.ApiGatewayApplication>(*args)
}
