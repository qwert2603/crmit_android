package com.qwert2603.crmit_android.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.qwert2603.crmit_android.db.converters.GroupBriefListConverter
import com.qwert2603.crmit_android.db.converters.StringListConverter
import com.qwert2603.crmit_android.db.generated_dao.*
import com.qwert2603.crmit_android.entity.*

@Database(
        entities = [
            Master::class,
            Teacher::class,
            StudentBrief::class,
            StudentFull::class,
            Section::class
        ],
        version = 1,
        exportSchema = true
)
@TypeConverters(GroupBriefListConverter::class, StringListConverter::class)
abstract class LocalDB : RoomDatabase() {
    abstract fun mastersDao(): MasterDao
    abstract fun teacherDao(): TeacherDao
    abstract fun studentBriefDao(): StudentBriefDao
    abstract fun studentFullDao(): StudentFullDao
    abstract fun sectionDao(): SectionDao
}