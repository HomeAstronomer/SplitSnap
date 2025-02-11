package com.splitsnap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.splitsnap.data.local.Converters
import com.splitsnap.data.local.ExpenseDao
import com.splitsnap.data.local.Group
import com.splitsnap.data.local.Expense
import com.splitsnap.data.local.GroupDao
import com.splitsnap.data.local.Member
import com.splitsnap.data.local.MemberDao

@Database(entities = [Group::class,
                     Member::class,
                     Expense::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao

    abstract fun groupDao():GroupDao

    abstract fun expenseDao():ExpenseDao
}