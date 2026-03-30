package com.splitsnap.feature.feature_Image_Acceptor

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splitsnap.feature.dashboard.DashboardUiState
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.FirebaseVertexAIException
import com.google.firebase.vertexai.type.content
import com.google.gson.Gson
import com.splitsnap.data.local.Expense
import com.splitsnap.data.local.Group
import com.splitsnap.data.local.Member
import com.splitsnap.data.repository.GroupRepository
import com.splitsnap.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TransactionDetails(
    val receiver: Receiver,
    val amount: Double,
    val time: String, // ISO 8601 format
    val transactionId: String,
    val platform: String
)

data class Receiver(
    val name: String,
    val upiId: String
)


@Immutable
data class ImageAcceptorUiState(
    var transactionDetails: TransactionDetails?=null,
    val groupList: List<Group> = emptyList(),
    val member: Member?=null
)

@HiltViewModel
class ImageAcceptorViewModel @Inject constructor(
    private var generativeModel: GenerativeModel,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
) : ViewModel()  {

    init {
        getGroupsFromDb()
        getMembersFromDb()
    }

    private val _uiState = MutableStateFlow(ImageAcceptorUiState())
    val uiState: StateFlow<ImageAcceptorUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImageAcceptorUiState()
    )

    private fun getMembersFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            memberRepository.getMemberDb().collect { memberList ->
                memberList?.getOrNull(0)?.let { firstMember->
                    _uiState.update { it.copy(member = firstMember) }
                }

            }
        }
    }
    fun setExpense(expense: Expense,group: Group,onDone:()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.member?.let { member ->
                groupRepository.addExpense(
                    group,
                    expense.copy(paidBy = member)
                ).collect {
                    onDone.invoke()
                }
            }
        }
    }
    private fun getGroupsFromDb() {
        viewModelScope.launch (Dispatchers.IO){
            groupRepository.getGroupsDb().collect{groupList->
                _uiState.update { it.copy(groupList = groupList) }
            }
        }
    }

    suspend fun detectImageData(bitmap: Bitmap):String{
        try {
            val prompt = content {
                image(bitmap)
                text("Extract Reciever Name ,Reciever UPI Id,Amount ,TransactionId,Time in iso")
            }
            val recognisedText=generativeModel.generateContent(prompt).text ?: ""
            val gson = Gson()
            val transactionDetails = gson.fromJson(recognisedText, TransactionDetails::class.java)
            _uiState.update {
                it.copy(transactionDetails=transactionDetails)
            }
            return recognisedText

        }catch (e: FirebaseVertexAIException){
            e.printStackTrace()
            return ""
        }


    }

    fun setTransactionModel(transactionModel:TransactionDetails){
        _uiState.update {
            it.copy(transactionDetails =transactionModel )
        }
    }
}