package com.local.data.repository.group

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.local.data.EXPENSES_COLLECTION
import com.local.data.FIELD_UPDATED_AT
import com.local.data.GROUPS_COLLECTION
import com.local.data.MEMBER_COLLECTION
import com.local.data.local.Expense
import com.local.data.local.Group
import com.local.data.local.Member
import com.local.data.local.toMap
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupRemoteDataSource @Inject constructor(
    private val fireStoreDb: FirebaseFirestore
) {

    fun getNewGroupId(): String {
        return fireStoreDb.collection(GROUPS_COLLECTION).document().id
    }

    suspend fun createGroup(group: Group, uid: String) {
        val groupMap = group.toMap()
        fireStoreDb.collection(GROUPS_COLLECTION)
            .document(group.id)
            .set(groupMap)
            .await()
        addGroupIdToMemberCreatedGroups(uid, group.id)
    }

    suspend fun getGroups(member: Member): List<Group> {
        val joinedGroups = member.joinedGroupIds
        val createdGroups = member.createdGroupIds
        val allGroups = joinedGroups + createdGroups

        if (allGroups.isEmpty()) {
            return emptyList()
        }

        val collection = fireStoreDb.collection(GROUPS_COLLECTION)
            .whereIn("id", allGroups)
            .get()
            .await()

        return collection.documents.mapNotNull { document ->
            document.toObject(Group::class.java)
        }
    }

    suspend fun joinGroup(member: Member, groupId: String): Member? {
        fireStoreDb.collection(GROUPS_COLLECTION)
            .document(groupId)
            .update("members", FieldValue.arrayUnion(member.toMap()))
            .await()
        return addGroupIdToMemberJoinedGroups(member.uid, groupId)
    }

    private suspend fun addGroupIdToMemberJoinedGroups(uid: String, groupId: String): Member? {
        fireStoreDb.collection(MEMBER_COLLECTION)
            .document(uid)
            .update("joinedGroupIds", FieldValue.arrayUnion(groupId))
            .await()

        val memberDocument = fireStoreDb.collection(MEMBER_COLLECTION)
            .document(uid)
            .get()
            .await()

        return memberDocument.toObject(Member::class.java)
    }

    private suspend fun addGroupIdToMemberCreatedGroups(uid: String, groupId: String) {
        fireStoreDb.collection(MEMBER_COLLECTION)
            .document(uid)
            .update("createdGroupIds", FieldValue.arrayUnion(groupId))
            .await()
    }

    suspend fun addExpense(group: Group, expense: Expense): Expense {
        val expenseId = fireStoreDb.collection(GROUPS_COLLECTION)
            .document(group.id)
            .collection(EXPENSES_COLLECTION)
            .document().id

        val updatedExpense = expense.copy(id = expenseId, groupId = group.id)
        val expenseMap = updatedExpense.toMap()

        fireStoreDb.collection(GROUPS_COLLECTION)
            .document(group.id)
            .collection(EXPENSES_COLLECTION)
            .document(expenseId)
            .set(expenseMap)
            .await()

        fireStoreDb.collection(GROUPS_COLLECTION)
            .document(group.id)
            .update(FIELD_UPDATED_AT, updatedExpense.updatedAt)
            .await()

        return updatedExpense
    }

    suspend fun getExpenses(groupId: String): List<Expense> {
        val expensesSnapshot = fireStoreDb.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(EXPENSES_COLLECTION)
            .get()
            .await()

        return expensesSnapshot.documents.mapNotNull { document ->
            document.toObject(Expense::class.java)?.copy(id = document.id)
        }
    }
}