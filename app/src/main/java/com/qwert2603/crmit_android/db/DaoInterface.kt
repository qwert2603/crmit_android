package com.qwert2603.crmit_android.db

interface DaoInterface<T> {

    fun addItems(items: List<T>)

    fun saveItem(item: T)

    fun getItem(itemId: Long): T?

    fun getItems(search: String = "", offset: Int = 0, count: Int = Int.MAX_VALUE): List<T>

    fun deleteAllItems()
}

fun <T> DaoInterface<T>.filterWhenGetItems(filterProvider: () -> ((T) -> Boolean)) = object : DaoInterface<T> by this {
    override fun getItems(search: String, offset: Int, count: Int): List<T> = this@filterWhenGetItems
            .getItems(search, offset, count)
            .filter(filterProvider())
}