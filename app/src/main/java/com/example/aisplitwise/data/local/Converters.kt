package com.example.aisplitwise.data.local

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken;

import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {

    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())




    // Converter for Timestamp (if using a simple Long for storage)
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }




    @TypeConverter
    fun fromStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromListString(list: List<String>): String {
        return gson.toJson(list)
    }

    // Converter for List<Member>
    @TypeConverter
    fun fromMemberList(value: String): List<Member> {
        val listType = object : TypeToken<List<Member>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromListMember(list: List<Member>): String {
        return gson.toJson(list)
    }

    // Converter for List<Expense>
    @TypeConverter
    fun fromExpenseList(value: String): List<Expense> {
        val listType = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromListExpense(list: List<Expense>): String {
        return gson.toJson(list)
    }

    // Converter for Timestamp to String
    @TypeConverter
    fun fromTimestamp(value: Timestamp): String {
        return dateFormat.format(value.toDate())
    }

    @TypeConverter
    fun stringToTimestamp(value: String): Timestamp {
        return Timestamp(dateFormat.parse(value)!!)
    }

    // Converter for Member (if used as a single object in Expense)
    @TypeConverter
    fun fromMember(value: String): Member {
        return gson.fromJson(value, Member::class.java)
    }

    @TypeConverter
    fun fromMemberToString(member: Member): String {
        return gson.toJson(member)
    }
}