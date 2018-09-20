// this file is auto-generated by com.qwert2603.dao_generator.GenerateDaoProcessor
package com.qwert2603.crmit_android.db.generated_dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.entity.Master

@Dao
interface MasterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<Master>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: Master)

    @Query("SELECT * FROM Master WHERE id = :itemId")
    fun getItem(itemId: Long): Master?

    @Query(
        " SELECT *" +
        " FROM Master" +
        " WHERE fio LIKE '%' || :search || '%'" +
        " ORDER BY id" +
        " LIMIT :count" +
        " OFFSET :offset"
    )
    fun getItems(search: String, offset: Int, count: Int): List<Master>

    @Query("DELETE FROM Master ")
    fun deleteAllItems()

    @Query("DELETE FROM Master")
    fun clearTable()
}

fun MasterDao.wrap(): DaoInterface<Master> = MasterDaoWrapper( this)

private class MasterDaoWrapper( private val masterDao: MasterDao) : DaoInterface<Master> {
    override fun addItems(items: List<Master>) = masterDao.addItems(items)
    override fun saveItem(item: Master) = masterDao.saveItem(item)
    override fun getItem(itemId: Long): Master? = masterDao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<Master> = masterDao.getItems(search, offset, count)
    override fun deleteAllItems() = masterDao.deleteAllItems()
}