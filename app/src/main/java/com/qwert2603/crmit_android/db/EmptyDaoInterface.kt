package com.qwert2603.crmit_android.db

class EmptyDaoInterface<T> : DaoInterface<T> {
    override fun addItems(items: List<T>) = Unit
    override fun saveItem(item: T) = Unit
    override fun getItem(itemId: Long): T? = null
    override fun getItems(search: String, offset: Int, count: Int): List<T> = emptyList()
    override fun deleteAllItems() = Unit
}