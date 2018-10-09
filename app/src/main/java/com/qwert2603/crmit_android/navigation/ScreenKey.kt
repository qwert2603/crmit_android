package com.qwert2603.crmit_android.navigation

enum class ScreenKey(val allowDrawer: Boolean = true) {
    SECTIONS,
    GROUPS,
    TEACHERS,
    MASTERS,
    STUDENTS,
    STUDENT_DETAILS,
    SECTION_DETAILS,
    GROUP_DETAILS,
    TEACHER_DETAILS,
    MASTER_DETAILS,
    ABOUT,
    STUDENTS_IN_GROUP,
    LESSONS_IN_GROUP,
    LESSON_DETAILS,
    PAYMENTS_IN_GROUP,
    GREETING(allowDrawer = false),
    LOGIN(allowDrawer = false),
    CABINET,

    LAST_SEENS,
    ACCESS_TOKENS
}