package com.example.aisplitwise.data.local

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize
import java.util.Date

const val GROUP_TABLE="group_table"

@Parcelize
@Stable
@Entity(tableName = GROUP_TABLE)
data class Group(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val members: List<Member> = emptyList(),  // Default value as an empty list
    var expenses: List<Expense> = emptyList(),  // Default value as an empty list
    val createdAt: Timestamp = Timestamp(Date()),
    val updatedAt: Timestamp = Timestamp(Date()),
    val groupImg:String=""
):Parcelable


fun Group.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "name" to name,
        "members" to members.map { it.toMap() },
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "groupImg" to groupImg
    )
}

@Parcelize
@Stable
data class Expense(
    val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val paidBy: Member = Member(),       // Default value using the no-argument constructor of Member
    val splitAmong: List<Member> = emptyList(),  // Default value as an empty list
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
):Parcelable



fun Expense.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "description" to description,
        "amount" to amount,
        "paidBy" to paidBy.toMap(),
        "splitAmong" to splitAmong.map { it.toMap() },
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}
@Dao
interface GroupDao {
    @Query("SELECT * FROM $GROUP_TABLE")
    fun getAll(): List<Group>

    @Delete
    fun delete(user: Member)

    @Query("SELECT * FROM $GROUP_TABLE")
    fun getAllFlow(): Flow<List<Group>>

    @Insert
    fun insert( group: Group)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(groups:List<Group>)
}