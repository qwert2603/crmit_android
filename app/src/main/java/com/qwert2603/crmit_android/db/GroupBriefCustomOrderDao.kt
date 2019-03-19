package com.qwert2603.crmit_android.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.entity.*

@Dao
interface GroupBriefCustomOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<GroupBrief>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: GroupBrief)

    @Query("SELECT * FROM GroupBrief WHERE id = :itemId")
    fun getItem(itemId: Long): GroupBrief?

    @Query("SELECT * FROM GroupBrief WHERE name LIKE '%' || :search || '%' ORDER BY teacherId!=:authedTeacherId, startMonth DESC, name LIMIT :count OFFSET :offset")
    fun getItems(authedTeacherId: Long, search: String, offset: Int, count: Int): List<GroupBrief>

    @Query("DELETE FROM GroupBrief ")
    fun deleteAllItems()

    @Query("DELETE FROM GroupBrief")
    fun clearTable()
}

fun GroupBriefCustomOrderDao.wrap(loginResult: LoginResult?): DaoInterface<GroupBrief> = GroupBriefCustomOrderDaoWrapper(loginResult, this)

private class GroupBriefCustomOrderDaoWrapper(loginResult: LoginResult?, private val groupBriefDao: GroupBriefCustomOrderDao) : DaoInterface<GroupBrief> {

    private val authedTeacherId = when (loginResult?.accountType) {
        AccountType.MASTER -> IdentifiableLong.NO_ID
        AccountType.TEACHER -> loginResult.detailsId
        AccountType.DEVELOPER -> IdentifiableLong.NO_ID
        AccountType.BOT -> throw BotAccountIsNotSupportedException()
        AccountType.STUDENT -> throw StudentAccountIsNotSupportedException()
        null -> IdentifiableLong.NO_ID
    }

    override fun addItems(items: List<GroupBrief>) = groupBriefDao.addItems(items)
    override fun saveItem(item: GroupBrief) = groupBriefDao.saveItem(item)
    override fun getItem(itemId: Long): GroupBrief? = groupBriefDao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<GroupBrief> = groupBriefDao.getItems(authedTeacherId, search, offset, count)
    override fun deleteAllItems() = groupBriefDao.deleteAllItems()
}