package com.example.aisplitwise.feature.dashboard

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.data.local.Expense
import com.example.aisplitwise.data.local.Group
import com.example.aisplitwise.data.local.Member
import com.example.aisplitwise.data.local.toMap
import com.example.aisplitwise.utils.MEMBER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

const val FIREBASE_TAG = "firebase"

@Immutable
data class DashboardUiState(
    val groupList:List<Group> = emptyList()

)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fireStoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {



    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            localDataSource.getAllFlow().collect { data ->
//                _uiState.update { it.copy(users = data) }
//
//            }
//        }
       getListOfMemberGroups {member->
               getGroups(member)
       }

        Log.d(FIREBASE_TAG, "init")


//        fireBaseInitWithDummyData()
    }

    private fun fireBaseInitWithDummyData() {

        val groupId = fireStoreDb.collection("groups").document().id
        val members = listOf(
            Member(uid = "userId1", displayName = "John Doe", email = "john@example.com"),
            Member(
                uid = "userId2",
                displayName = "Jane Smith",
                email = "jane@example.com"
            )
        )
        val group = Group(
            id = getNewgroupId(),
            name = "The Boys",
            members = listOf(
                Member(
                    uid = "",
                    displayName = "",
                    email = "",
                    phoneNumber = "",
                    photoUrl = "",
                    createdGroupIds = emptyList(),
                    joinedGroupIds = emptyList()
                )
            )
        )

        createGroup(group, {}, {},"")
        addExpense(groupId, members)

        getListOfMemberGroups {member->
            getGroups(member)
        }


    }

    fun getNewgroupId(): String {
        return fireStoreDb.collection("groups").document().id
    }

    fun getGroups(member: Member) {

        val joinedGroups = member.joinedGroupIds
        val createdGroups = member.createdGroupIds
        val allGroups = joinedGroups + createdGroups
        if(allGroups.isNotEmpty() ) {
            fireStoreDb.collection("groups")
                .whereIn("id", allGroups)
                .get()
                .addOnSuccessListener { collection ->
                    if (!collection.isEmpty) {
                        val groups = collection.documents.mapNotNull { document ->
                            document.toObject(Group::class.java)
                        }

                        _uiState.update { it.copy(groupList = groups) }

                    } else {
                        Log.e(FIREBASE_TAG,"Empty Collection")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(FIREBASE_TAG,e.message?:"")
                }
        }
    }

    fun getListOfMemberGroups(onSuccess:(Member)->Unit) {

        fireStoreDb.collection(MEMBER_COLLECTION).document(firebaseAuth.uid ?: "").get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val member = document.toObject(Member::class.java)
                    member?.let {
                        onSuccess.invoke(it)
                    }
                }
            }
            .addOnFailureListener {

            }

    }

    private fun addExpense(groupId: String, members: List<Member>) {

        val expenseId =
            fireStoreDb.collection("groups").document(groupId).collection("expenses").document().id


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

    }

    fun createGroup(
        group: Group,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        uid:String
    ) {
        val groupMap = group.copy(expenses = emptyList()).toMap()
        fireStoreDb.collection("groups")
            .document(group.id)
            .set(groupMap)
            .addOnSuccessListener {
                // After the group is created, add the group ID to the Member's createdGroupIds
                fireStoreDb.collection(MEMBER_COLLECTION)
                    .document(uid) // Assuming "members" collection and memberId identifies the member
                    .update("createdGroupIds", FieldValue.arrayUnion(group.id))
                    .addOnSuccessListener {
                        onSuccess.invoke()
                    }
                    .addOnFailureListener { e ->
                        onFailure.invoke(e.message ?: "Something Went Wrong")
                    }
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e.message ?: "Something Went Wrong")
            }

    }


}