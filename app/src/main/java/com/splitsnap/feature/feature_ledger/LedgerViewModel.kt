package com.splitsnap.feature.feature_ledger

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.splitsnap.navigation.LedgerRoute
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



@Immutable
data class LedgerUIState(
    val group:Group?=null,
    val member:Member?=null,
    val expense: List<Expense> =emptyList(),
    val  moneyStatus:Pair<Double, Double> = Pair(0.0,0.0)

)

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository
): ViewModel()  {
    val expenseFlow: StateFlow<Expense?> = savedStateHandle.getStateFlow("expenseKey", null)
    private val _uiState = MutableStateFlow(LedgerUIState())
    val uiState: StateFlow<LedgerUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LedgerUIState()
    )
    init {
        getGroup(savedStateHandle.toRoute<LedgerRoute>().groupId)
        getMembersFromDb()

    }

    private fun getGroup(groupId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            groupRepository.getGroupFromID(groupId).collect{group->
                _uiState.update { it.copy(group = group) }
            }

        }
        viewModelScope.launch(Dispatchers.IO) {
           groupRepository.getExpenseFromGroupId(groupId).collect{expense->
                _uiState.update { it.copy(expense = expense) }

            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.getExpenses(groupId =groupId )
        }

        viewModelScope.launch(Dispatchers.IO){
            groupRepository.getExpenseFromGroupId(groupId).combine( memberRepository.getMemberDb()){expenses,member->
                member.getOrNull(0)?.let {
                    val moneyStatus=calculateMoneyStatus(it, expenses)
                    _uiState.update { it.copy(moneyStatus = moneyStatus) }
                }
            }.collect{}
        }
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

    fun setExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.member?.let { member ->
                groupRepository.addExpense(
                    uiState.value.group!!,
                    expense.copy(paidBy = member)
                ).collect {}
            }
        }
    }

    fun calculateMoneyStatus(member: Member, expenses: List<Expense>): Pair<Double, Double> {
        val moneyToBeRecievdMap= mutableMapOf<String,Float>()
        val moneyToBeSpentMap= mutableMapOf<String, Float>()

        for (expense in expenses){
            val share=expense.amount.toFloat()/expense.splitAmong.size
            if(expense.paidBy.uid==member.uid){
                for(memberLoop in expense.splitAmong) {
                    if(memberLoop.uid!=member.uid){
                        val moneyToBeSentToUser=moneyToBeSpentMap.getOrDefault(memberLoop.uid,0.0f)
                        val moneyToBeRecievedFromUser=moneyToBeRecievdMap.getOrDefault(memberLoop.uid,0.0f)+ share
                        if(moneyToBeSentToUser>moneyToBeRecievedFromUser){
                            moneyToBeSpentMap[memberLoop.uid]=moneyToBeSentToUser-moneyToBeRecievedFromUser
                        }else{
                            moneyToBeRecievdMap[memberLoop.uid]=moneyToBeRecievedFromUser-moneyToBeSentToUser
                        }
                    }
                }
            }else{
                val moneyToBeSentToUser=moneyToBeSpentMap.getOrDefault(expense.paidBy.uid,0.0f)+ share
                val moneyToBeRecievedFromUser=moneyToBeRecievdMap.getOrDefault(expense.paidBy.uid,0.0f)
                if(moneyToBeSentToUser>moneyToBeRecievedFromUser){
                    moneyToBeSpentMap[expense.paidBy.uid]=moneyToBeSentToUser-moneyToBeRecievedFromUser
                }else{
                    moneyToBeRecievdMap[expense.paidBy.uid]=moneyToBeRecievedFromUser-moneyToBeSentToUser
                }

            }
        }
        val moneyToBeRecieved=moneyToBeRecievdMap.values.sum()
        val moneyToBeSpent=moneyToBeSpentMap.values.sum()
        return Pair( moneyToBeSpent.toDouble(),moneyToBeRecieved.toDouble())
    }
}