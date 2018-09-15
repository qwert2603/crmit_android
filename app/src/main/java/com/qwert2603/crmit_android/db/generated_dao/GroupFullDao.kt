// this file is auto-generated by com.qwert2603.dao_generator.GenerateDaoProcessor
package com.qwert2603.crmit_android.db.generated_dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.entity.GroupFull

@Dao
interface GroupFullDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<GroupFull>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: GroupFull)

    @Query("SELECT * FROM GroupFull WHERE id = :itemId")
    fun getItem(itemId: Long): GroupFull?

    @Query(
        " SELECT *" +
        " FROM GroupFull" +
        " WHERE name LIKE '%' || :search || '%'" +
        " ORDER BY id" +
        " LIMIT :count" +
        " OFFSET :offset"
    )
    fun getItems(search: String, offset: Int, count: Int): List<GroupFull>

    @Query("DELETE FROM GroupFull")
    fun deleteAllItems()
}

fun GroupFullDao.wrap() = GroupFullDaoWrapper(this)

class GroupFullDaoWrapper(private val groupFullDao: GroupFullDao) : DaoInterface<GroupFull> {
    override fun addItems(items: List<GroupFull>) = groupFullDao.addItems(items)
    override fun saveItem(item: GroupFull) = groupFullDao.saveItem(item)
    override fun getItem(itemId: Long): GroupFull? = groupFullDao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<GroupFull> = groupFullDao.getItems(search, offset, count)
    override fun deleteAllItems() = groupFullDao.deleteAllItems()
}