package com.aej.screen.routing.data

data class PagingData<T>(
    var count: Long,
    var countPerPage: Long,
    var currentPage: Long,
    var data: List<T>
)
