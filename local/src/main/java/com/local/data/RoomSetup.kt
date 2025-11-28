package com.local.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.local.data.local.Converters
import com.local.data.local.ExpenseDao
import com.local.data.local.Group
import com.local.data.local.Expense
import com.local.data.local.GroupDao
import com.local.data.local.Member
import com.local.data.local.MemberDao

@Database(entities = [Group::class,
                     Member::class,
                     Expense::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao

    abstract fun groupDao():GroupDao

    abstract fun expenseDao():ExpenseDao
}