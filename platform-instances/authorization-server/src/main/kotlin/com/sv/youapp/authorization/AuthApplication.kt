package com.sv.youapp.authorization

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthApplication

fun main(args: Array<String>) {
    runApplication<com.sv.youapp.authorization.AuthApplication>(*args)
}
