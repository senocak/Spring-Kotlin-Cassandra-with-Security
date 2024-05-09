package com.github.senocak.skc.domain

class PaginatedResponseTemplate<T> {
    var page = 0
    var size = 0
    var items: List<T>? = null
}
