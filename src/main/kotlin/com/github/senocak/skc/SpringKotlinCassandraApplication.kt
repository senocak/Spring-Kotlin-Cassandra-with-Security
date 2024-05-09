package com.github.senocak.skc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKotlinCassandraApplication

fun main(args: Array<String>) {
    runApplication<SpringKotlinCassandraApplication>(*args)
}
