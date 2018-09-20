// this file is auto-generated by com.qwert2603.dao_generator.GenerateDaoProcessor
package com.qwert2603.crmit_android.db.generated_dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.entity.GroupBrief

@Dao
interface GroupBriefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<GroupBrief>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: GroupBrief)

    @Query("SELECT * FROM GroupBrief WHERE id = :itemId")
    fun getItem(itemId: Long): GroupBrief?

    @Query(
        " SELECT *" +
        " FROM GroupBrief" +
        " WHERE name LIKE '%' || :search || '%'" +
        " ORDER BY id" +
        " LIMIT :count" +
        " OFFSET :offset"
    )
    fun getItems(search: String, offset: Int, count: Int): List<GroupBrief>

    @Query("DELETE FROM GroupBrief ")
    fun deleteAllItems()

    @Query("DELETE FROM GroupBrief")
    fun clearTable()
}

fun GroupBriefDao.wrap(): DaoInterface<GroupBrief> = GroupBriefDaoWrapper( this)

private class GroupBriefDaoWrapper( private val groupBriefDao: GroupBriefDao) : DaoInterface<GroupBrief> {
    override fun addItems(items: List<GroupBrief>) = groupBriefDao.addItems(items)
    override fun saveItem(item: GroupBrief) = groupBriefDao.saveItem(item)
    override fun getItem(itemId: Long): GroupBrief? = groupBriefDao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<GroupBrief> = groupBriefDao.getItems(search, offset, count)
    override fun deleteAllItems() = groupBriefDao.deleteAllItems()
}