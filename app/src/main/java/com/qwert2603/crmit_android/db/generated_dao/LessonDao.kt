// this file is auto-generated by com.qwert2603.dao_generator.GenerateDaoProcessor
package com.qwert2603.crmit_android.db.generated_dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.entity.Lesson

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<Lesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: Lesson)

    @Query("SELECT * FROM Lesson WHERE id = :itemId")
    fun getItem(itemId: Long): Lesson?

    @Query(
        " SELECT *" +
        " FROM Lesson" +
        " WHERE groupId = :groupId" +
        " ORDER BY date" +
        " LIMIT :count" +
        " OFFSET :offset"
    )
    fun getItems(groupId: Long, offset: Int, count: Int): List<Lesson>

    @Query("DELETE FROM Lesson WHERE groupId = :groupId")
    fun deleteAllItems(groupId: Long)

    @Query("DELETE FROM Lesson")
    fun clearTable()
}

fun LessonDao.wrap(groupId: Long): DaoInterface<Lesson> = LessonDaoWrapper(groupId,  this)

private class LessonDaoWrapper(private val groupId: Long,  private val lessonDao: LessonDao) : DaoInterface<Lesson> {
    override fun addItems(items: List<Lesson>) = lessonDao.addItems(items)
    override fun saveItem(item: Lesson) = lessonDao.saveItem(item)
    override fun getItem(itemId: Long): Lesson? = lessonDao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<Lesson> = lessonDao.getItems(groupId, offset, count)
    override fun deleteAllItems() = lessonDao.deleteAllItems(groupId)
}