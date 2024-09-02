package com.example.aisplitwise.feature.dashboard

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.User
import com.example.aisplitwise.UserDao
import com.example.aisplitwise.responseModel.Expense
import com.example.aisplitwise.responseModel.Group
import com.example.aisplitwise.responseModel.Member
import com.example.aisplitwise.responseModel.toMap
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
const val FIREBASE_TAG="firebase"

@Immutable
data class DashboardUiState(
    val box1:Color= Color.Red,
    val box2:Color=Color.Blue,
    val box3:Color=Color.Green,
    val users:List<User> = emptyList()

)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val localDataSource: UserDao,
    fireStoreDb:FirebaseFirestore,
): ViewModel() {

    val Rand=Random.Default
    fun add() {
        viewModelScope.launch (Dispatchers.IO) {
            localDataSource.insertAll(
                User(
                    uid = Rand.nextInt(),
                    firstName = "Atharva",
                    lastName = "Gorule"
                )
            )
        }

    }
    val state= localDataSource.getAllFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )


    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
        viewModelScope.launch (Dispatchers.IO){
            localDataSource.getAllFlow().collect{data->
              _uiState.update { it.copy(users = data) }

            }
        }
        Log.d(FIREBASE_TAG, "init")



        fireBaseInitWithDummyData(fireStoreDb)
    }

    private fun fireBaseInitWithDummyData(fireStoreDb: FirebaseFirestore) {
        val groupId = fireStoreDb.collection("groups").document().id
        val members = listOf(
            Member(id = "userId1", name = "John Doe", email = "john@example.com"),
            Member(
                id = "userId2",
                name = "Jane Smith",
                email = "jane@example.com"
            )
        )
        val group = Group(
            id = groupId,
            name = "Friends Trip",
            members = members,
            expenses = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

// Convert Group to a map to store in Firestore
        val groupMap = group.copy(expenses = emptyList()).toMap()

        fireStoreDb.collection("groups").document(groupId).set(groupMap)
            .addOnSuccessListener {
                // Group successfully created
            }
            .addOnFailureListener { e ->
                // Handle error
            }


        val expenseId = fireStoreDb.collection("groups").document(groupId).collection("expenses").document().id



        val expense = Expense(
            id = expenseId,
            description = "Dinner",
            amount = 100.0,
            paidBy = members[0],  // Assume the first member paid
            splitAmong = members,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

// Convert Expense to a map to store in Firestore
        val expenseMap = expense.toMap()

        fireStoreDb.collection("groups").document(groupId)
            .collection("expenses").document(expenseId).set(expenseMap)
            .addOnSuccessListener {
                // Expense successfully added
            }
            .addOnFailureListener { e ->
                // Handle error
            }


        fireStoreDb.collection("groups").document(groupId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Convert the document to a Group object
                    val group = document.toObject(Group::class.java)
                    group?.let {
                        // Now retrieve the expenses sub-collection
                        fireStoreDb.collection("groups").document(groupId).collection("expenses").get()
                            .addOnSuccessListener { expenseDocuments ->
                                val expenses = expenseDocuments.toObjects(Expense::class.java)
                                group.expenses = expenses

                                // Now you have the complete Group object with expenses
                                // Use the group object as needed
                            }
                            .addOnFailureListener { e ->
                                // Handle error retrieving expenses
                            }
                    }
                } else {
                    // Handle case where group doesn't exist
                }
            }
            .addOnFailureListener { e ->
                // Handle error retrieving group
            }
    }

    fun updateColor1(){
        if(_uiState.value.box1== Color.Blue){
            _uiState.update { it.copy(box1 = Color.Red) }
        }else{
            _uiState.update { it.copy(box1 = Color.Blue) }
        }

    }
    fun updateColor2(){
        if(_uiState.value.box2== Color.Green){
            _uiState.update { it.copy(box2 = Color.Cyan) }
        }else{
            _uiState.update { it.copy(box2 = Color.Green) }
        }
    }

}