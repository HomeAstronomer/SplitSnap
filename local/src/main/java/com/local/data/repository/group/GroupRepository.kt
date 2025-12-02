package com.local.data.repository.group

import com.google.firebase.Timestamp
import com.local.data.local.Expense
import com.local.data.local.ExpenseDao
import com.local.data.local.Group
import com.local.data.local.GroupDao
import com.local.data.local.Member
import com.local.data.local.MemberDao
import com.local.data.repository.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val groupDao: GroupDao,
    private val memberDao: MemberDao,
    private val expenseDao: ExpenseDao,
    private val groupRemoteDataSource: GroupRemoteDataSource
) {
    fun getGroupsDb(): Flow<List<Group>> {
        return groupDao.getAllFlow()
    }

    fun getGroupFromID(groupId: String): Flow<Group> {
        return groupDao.getGroup(groupId)
    }

    fun getExpenseFromGroupId(groupId: String): Flow<List<Expense>> {
        return expenseDao.getExpenseForGroup(groupId)
    }

    fun getGroups(member: Member): Flow<DataState<List<Group>>> = flow {
        try {
            val groups = groupRemoteDataSource.getGroups(member)
            groupDao.insertAll(groups)
            emit(DataState.Success(groups))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Error fetching groups"))
        }
    }

    fun getNewGroupId(): String {
        return groupRemoteDataSource.getNewGroupId()
    }

    fun createGroup(group: Group, uid: String): Flow<DataState<Unit>> = flow {
        try {
            groupRemoteDataSource.createGroup(group, uid)
            groupDao.insert(group)
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Something Went Wrong"))
        }
    }

    fun joinGroup(member: Member, groupId: String): Flow<DataState<Unit>> = flow {
        try {
            val updatedMember = groupRemoteDataSource.joinGroup(member, groupId)
            updatedMember?.let {
                getGroups(it)
                memberDao.insertMember(it)
            }
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Something Went Wrong"))
        }
    }

    fun addExpense(group: Group, expense: Expense): Flow<DataState<Unit>> = flow {
        try {
            val updatedExpense = groupRemoteDataSource.addExpense(group, expense)
            expenseDao.insert(updatedExpense)
            groupDao.updateExpensesAndTimestamp(group.id, Timestamp(Date()))
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Something Went Wrong"))
        }
    }

    fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return dateFormat.format(Date())
    }

    suspend fun getExpenses(groupId: String): List<Expense> {
        return try {
            val expenses = groupRemoteDataSource.getExpenses(groupId)
            expenseDao.insertAll(expenses)
            expenses
        } catch (e: Exception) {
            emptyList()
        }
    }
}