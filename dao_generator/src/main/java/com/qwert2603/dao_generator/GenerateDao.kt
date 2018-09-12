package com.qwert2603.dao_generator

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateDao(
        val searchField: String
)