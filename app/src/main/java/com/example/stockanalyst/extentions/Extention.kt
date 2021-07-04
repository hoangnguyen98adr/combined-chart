package com.example.stockanalyst.extentions

fun <T> List<T>.safeSublist(fromIndex: Int, toIndex: Int) : List<T> =
    this.subList(fromIndex.coerceAtLeast(0), toIndex.coerceAtMost(this.size))