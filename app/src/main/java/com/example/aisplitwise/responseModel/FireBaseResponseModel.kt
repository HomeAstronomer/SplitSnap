package com.example.aisplitwise.responseModel

import androidx.compose.runtime.Stable
import androidx.room.Entity

data class Group(
    val id: String = "",
    val name: String = "",
    val members: List<Member> = emptyList(),  // Default value as an empty list
    var expenses: List<Expense> = emptyList(),  // Default value as an empty list
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
data class Member(
    val id: String = "",     // Default value for id
    val name: String = "",   // Default value for name
    val email: String = ""   // Default value for email
)

data class Expense(
    val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val paidBy: Member = Member(),       // Default value using the no-argument constructor of Member
    val splitAmong: List<Member> = emptyList(),  // Default value as an empty list
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

fun Group.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "name" to name,
        "members" to members.map { it.toMap() },
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

fun Member.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "name" to name,
        "email" to email
    )
}

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
