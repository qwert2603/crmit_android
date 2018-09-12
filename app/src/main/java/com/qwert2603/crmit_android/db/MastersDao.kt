package com.qwert2603.crmit_android.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.entity.Master

@Dao
interface MastersDao {

    @Insert
    fun addItems(items: List<Master>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: Master)

    @Query("SELECT * FROM Master WHERE id = :itemId")
    fun getItem(itemId: Long): Master

    @Query("SELECT * FROM Master WHERE fio LIKE '%' || :search || '%' LIMIT :count OFFSET :offset")
    fun getItems(search: String, offset: Int, count: Int): List<Master>

    @Query("DELETE FROM Master")
    fun deleteAllItems()
}

class MastersDaoWrapper(private val mastersDao: MastersDao) : DaoInterface<Master> {
    override fun addItems(items: List<Master>) = mastersDao.addItems(items)
    override fun saveItem(item: Master) = mastersDao.saveItem(item)
    override fun getItem(itemId: Long): Master = mastersDao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<Master> = mastersDao.getItems(search, offset, count)
    override fun deleteAllItems() = mastersDao.deleteAllItems()
}