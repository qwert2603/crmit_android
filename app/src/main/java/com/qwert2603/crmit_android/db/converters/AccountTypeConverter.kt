package com.qwert2603.crmit_android.db.converters

import androidx.room.TypeConverter
import com.qwert2603.crmit_android.entity.AccountType

class AccountTypeConverter {
    @TypeConverter
    fun toAccountType(accountType: AccountType): String = accountType.name

    @TypeConverter
    fun fromAccountType(name: String): AccountType = AccountType.valueOf(name)
}