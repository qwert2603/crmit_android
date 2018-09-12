package com.qwert2603.crmit_android.db

interface DaoInterface<T> {

    fun addItems(items: List<T>)

    fun saveItem(item: T)

    fun getItem(itemId: Long): T

    fun getItems(search: String, offset: Int, count: Int): List<T>

    fun deleteAllItems()
}