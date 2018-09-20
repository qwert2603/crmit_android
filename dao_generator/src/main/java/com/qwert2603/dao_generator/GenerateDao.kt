package com.qwert2603.dao_generator

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateDao(
        val searchField: String = "",
        val filters: Array<Filter> = [],
        val orderBy: String = "id"
)

@Target()
@Retention(AnnotationRetention.SOURCE)
annotation class Filter(
        val fieldName: String,
        val filterType: FilterType
)


enum class FilterType(val typeString: String) {
    LONG("Long"),
    STRING("String")
}