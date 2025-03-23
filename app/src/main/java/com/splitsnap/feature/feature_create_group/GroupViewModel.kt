package com.splitsnap.feature.feature_create_group

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splitsnap.data.local.Group
import com.splitsnap.data.local.Member
import com.splitsnap.data.repository.DataState
import com.splitsnap.data.repository.GroupRepository
import com.splitsnap.data.repository.MemberRepository
import com.splitsnap.feature.dashboard.DashboardUiState
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import java.util.Date
import javax.inject.Inject

@Immutable
data class CreateGroupUIState(
    val imgList: List<String> =listOf(
        "https://firebasestorage.googleapis.com/v0/b/splitsnap-8c640.firebasestorage.app/o/groupImages%2Fairplane_1350120.png?alt=media&token=04b4b4f8-8b1a-4385-9815-88bf562e6d95",
        "https://firebasestorage.googleapis.com/v0/b/splitsnap-8c640.firebasestorage.app/o/groupImages%2Fbull-market.png?alt=media&token=8f2ebcf1-69fd-47ba-a12b-e08886bec0c5",
        "https://firebasestorage.googleapis.com/v0/b/splitsnap-8c640.firebasestorage.app/o/groupImages%2Fcake_918234.png?alt=media&token=06b64f1c-aaf8-4ede-8fe5-a843e161814f",
        "https://firebasestorage.googleapis.com/v0/b/splitsnap-8c640.firebasestorage.app/o/groupImages%2Fcampfire_6154686.png?alt=media&token=dd5feb9c-0a07-452d-ada1-37023d7e25ea",
    ),
    val showLoader:Boolean=false,
    val member:Member?=null,
    val showToast:Boolean=false,
    val toastMessage:String=""

)

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    init {
        getallImageURI()
        getMembersFromDb()
    }

    private fun getMembersFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            memberRepository.getMemberDb().collect { memberList ->
                memberList.getOrNull(0)?.let { firstMember->
                    _uiState.update { it.copy(member = firstMember) }

                }

            }
        }
    }


    private val _uiState = MutableStateFlow(CreateGroupUIState())
    val uiState: StateFlow<CreateGroupUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CreateGroupUIState()
    )
    private fun getallImageURI() {
        viewModelScope.launch (Dispatchers.IO) {
            getAllImageUrisFromFirebase("groupImages/imgJsonList.json")
        }
    }

    suspend fun getAllImageUrisFromFirebase(folderPath: String): List<String> {
        // Reference to Firebase Storage
        val storageReference: StorageReference = firebaseStorage.reference.child(folderPath)

        // List to store image URIs
        val imageUris = ArrayList<String>()

        try {
            // Get the download URL of the JSON file
            val jsonUri = storageReference.downloadUrl.await()

            // Download the JSON file's content
            val jsonString = withContext(Dispatchers.IO) {
                URL(jsonUri.toString()).readText()
            }

            // Parse the JSON to extract the URIs
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                imageUris.add(jsonArray.getString(i))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any errors (e.g., file not found, network issues)
        }

        _uiState.update { it.copy(imgList = imageUris) }
        return imageUris
    }

    fun createGroup(groupName: String, groupImage: String, onSuccess: () -> Unit) {
        _uiState.update { it.copy(showLoader = true) }
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value.member?.let {
                val group = Group(
                    id = getNewGroupId(),
                    name = groupName,
                    members = listOf(it),
                    createdAt = Timestamp(Date()),
                    updatedAt = Timestamp(Date()),
                    groupImg = groupImage
                )
                groupRepository.createGroup(group, it.uid).collect { dataState ->
                    _uiState.update { it.copy(showLoader = false) }
                    when (dataState) {
                        is DataState.Success -> {
                            _uiState.update {uistate->
                                uistate.copy(showToast = true, toastMessage = "Group Created Successfully")
                            }
                            withContext(Dispatchers.Main) {
                                onSuccess.invoke()
                            }
                        }

                        is DataState.Error -> {
                            _uiState.update {uistate->
                                uistate.copy(
                                    showToast = true,
                                    toastMessage = dataState.errorMessage
                                )
                            }
                        }
                    }

                }
            }
        }

    }
    fun getNewGroupId(): String {
        return groupRepository.getNewGroupId()
    }

}