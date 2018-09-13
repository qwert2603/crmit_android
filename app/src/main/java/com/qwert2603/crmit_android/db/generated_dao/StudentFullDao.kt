// this file is auto-generated by com.qwert2603.dao_generator.GenerateDaoProcessor
package com.qwert2603.crmit_android.db.generated_dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.entity.StudentFull

@Dao
interface StudentFullDao {

    @Insert
    fun addItems(items: List<StudentFull>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: StudentFull)

    @Query("SELECT * FROM StudentFull WHERE id = :itemId")
    fun getItem(itemId: Long): StudentFull

    @Query("SELECT * FROM StudentFull WHERE fio LIKE '%' || :search || '%' LIMIT :count OFFSET :offset")
    fun getItems(search: String, offset: Int, count: Int): List<StudentFull>

    @Query("DELETE FROM StudentFull")
    fun deleteAllItems()
}

fun StudentFullDao.wrap() = StudentFullDaoWrapper(this)

class StudentFullDaoWrapper(private val studentFullDao: StudentFullDao) : DaoInterface<StudentFull> {
    override fun addItems(items: List<StudentFull>) = studentFullDao.addItems(items)
    override fun saveItem(item: StudentFull) = studentFullDao.saveItem(item)
    override fun getItem(itemId: Long): StudentFull = studentFullDao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<StudentFull> = studentFullDao.getItems(search, offset, count)
    override fun deleteAllItems() = studentFullDao.deleteAllItems()
}