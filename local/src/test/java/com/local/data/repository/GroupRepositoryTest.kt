
package com.local.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.local.data.local.Expense
import com.local.data.local.Group
import com.local.data.local.toMap
import com.local.data.repository.group.GroupRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.Locale

class GroupRepositoryTest : BaseRepositoryTest() {

    private lateinit var groupRepository: GroupRepository

    @Before
    fun setUp() {
        groupRepository = GroupRepository(
            groupDao,
            memberDao,
            expenseDao,
            fireStoreDb
        )
    }

    @Test
    fun `getGroupsDb should return groups from dao`() = runBlocking {
        val groups = listOf(Group(id = "1", name = "Test Group"))
        Mockito.`when`(groupDao.getAllFlow()).thenReturn(flowOf(groups))

        val result = groupRepository.getGroupsDb().first()

        assert(result == groups)
    }

    @Test
    fun `createGroup should add group to firestore and dao`() = runBlocking {
        val group = Group(id = "1", name = "Test Group")
        val uid = "test_uid"

        groupRepository.createGroup(group, uid).first()

        Mockito.verify(fireStoreDb.collection("groups").document(group.id)).set(group.toMap())
        Mockito.verify(groupDao).insert(group)
    }

    @Test
    fun `getCurrentTimestamp should return a valid timestamp`() {
        val timestamp = groupRepository.getCurrentTimestamp()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        try {
            dateFormat.parse(timestamp)
            assert(true)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `getGroupFromID should return group from dao`() = runBlocking {
        val group = Group(id = "1", name = "Test Group")
        Mockito.`when`(groupDao.getGroup("1")).thenReturn(flowOf(group))

        val result = groupRepository.getGroupFromID("1").first()

        assert(result == group)
    }

    @Test
    fun `getExpenseFromGroupId should return expenses from dao`() = runBlocking {
        val expenses = listOf(Expense(id = "1", groupId = "1", description = "Test Expense"))
        Mockito.`when`(expenseDao.getExpenseForGroup("1")).thenReturn(flowOf(expenses))

        val result = groupRepository.getExpenseFromGroupId("1").first()

        assert(result == expenses)
    }

    @Test
    fun `getNewGroupId should return a new group id`() {
        val mockDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockCollectionReference = Mockito.mock(CollectionReference::class.java)

        Mockito.`when`(fireStoreDb.collection("groups")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
        Mockito.`when`(mockDocumentReference.id).thenReturn("new_group_id")

        val result = groupRepository.getNewGroupId()

        assert(result == "new_group_id")
    }

    @Test
    fun `addExpense should add expense to firestore and dao`() = runBlocking {
        val group = Group(id = "1", name = "Test Group")
        val expense = Expense(id = "1", groupId = "1", description = "Test Expense")

        val mockExpenseDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockGroupDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockExpensesCollectionReference = Mockito.mock(CollectionReference::class.java)

        Mockito.`when`(fireStoreDb.collection("groups")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(group.id)).thenReturn(mockGroupDocumentReference)
        Mockito.`when`(mockGroupDocumentReference.collection("expenses")).thenReturn(mockExpensesCollectionReference)
        Mockito.`when`(mockExpensesCollectionReference.document()).thenReturn(mockExpenseDocumentReference)
        Mockito.`when`(mockExpenseDocumentReference.id).thenReturn("new_expense_id")

        val setTask = Tasks.forResult(null)
        Mockito.`when`(mockExpenseDocumentReference.set(anyMap<String, Any>())).thenReturn(setTask as com.google.android.gms.tasks.Task<Void>)

        val updateTask = Tasks.forResult(null)
        Mockito.`when`(mockGroupDocumentReference.update(any(String::class.java), any())).thenReturn(updateTask as com.google.android.gms.tasks.Task<Void>)

        groupRepository.addExpense(group, expense).first()

        val updatedExpense = expense.copy(id = "new_expense_id", groupId = group.id)
        val expenseMap = updatedExpense.toMap()

        Mockito.verify(mockExpenseDocumentReference).set(expenseMap)
        Mockito.verify(mockGroupDocumentReference).update(Mockito.eq("updatedAt"), any())
        Mockito.verify(expenseDao).insert(updatedExpense)
        Mockito.verify(groupDao).updateExpensesAndTimestamp(Mockito.eq(group.id), any(Timestamp::class.java))
    }

    @Test
    fun `getExpenses should return expenses from firestore`() = runBlocking {
        val groupId = "1"
        val expense = Expense(id = "expense1", groupId = groupId, description = "Test Expense")
        val mockDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
        val mockQuerySnapshot = Mockito.mock(QuerySnapshot::class.java)
        val mockCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockDocumentReference = Mockito.mock(DocumentReference::class.java)

        Mockito.`when`(fireStoreDb.collection("groups")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(groupId)).thenReturn(mockDocumentReference)
        Mockito.`when`(mockDocumentReference.collection("expenses")).thenReturn(mockCollectionReference)

        val successfulTask = Tasks.forResult(mockQuerySnapshot)
        Mockito.`when`(mockCollectionReference.get()).thenReturn(successfulTask)

        Mockito.`when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
        Mockito.`when`(mockDocumentSnapshot.toObject(Expense::class.java)).thenReturn(expense)
        Mockito.`when`(mockDocumentSnapshot.id).thenReturn("expense1")

        val result = groupRepository.getExpenses(groupId)

        assert(result.size == 1)
        assert(result[0] == expense.copy(id = "expense1"))
        Mockito.verify(expenseDao).insertAll(listOf(expense.copy(id = "expense1")))
    }
}
