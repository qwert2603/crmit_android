package com.qwert2603.crmit_android.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
            Section::class,
            GroupBrief::class,
            GroupFull::class,
            StudentInGroup::class,
            Lesson::class,
            Attending::class,
            Payment::class
        ],
        version = 3,
        exportSchema = true
)
@TypeConverters(GroupBriefListConverter::class, StringListConverter::class)
abstract class LocalDB : RoomDatabase() {
    abstract fun masterDao(): MasterDao
    abstract fun teacherDao(): TeacherDao
    abstract fun studentBriefDao(): StudentBriefDao
    abstract fun studentFullDao(): StudentFullDao
    abstract fun sectionDao(): SectionDao
    abstract fun groupFullDao(): GroupFullDao
    abstract fun studentInGroupDao(): StudentInGroupDao
    abstract fun lessonDao(): LessonDao
    abstract fun attendingDao(): AttendingDao
    abstract fun paymentDao(): PaymentDao

    abstract fun groupBriefCustomOrderDao(): GroupBriefCustomOrderDao
    abstract fun lastLessonDao(): LastLessonsDao
}