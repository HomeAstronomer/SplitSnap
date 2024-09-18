package com.example.aisplitwise.feature.feature_ledger

import android.provider.ContactsContract
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.aisplitwise.LedgerRoute
import com.example.aisplitwise.data.local.Group
import com.example.aisplitwise.data.local.Member
import com.example.aisplitwise.data.repository.GroupRepository
import com.example.aisplitwise.feature.feature_create_group.CreateGroupUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



@Immutable
data class LedgerUIState(
    val group:Group?=null

)

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
): ViewModel()  {

    private val _uiState = MutableStateFlow(LedgerUIState())
    val uiState: StateFlow<LedgerUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LedgerUIState()
    )
    init {
        getGroup(savedStateHandle.toRoute<LedgerRoute>().groupId)
    }

    private fun getGroup(groupId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            groupRepository.getGroupFromID(groupId).collect{group->
                _uiState.update { it.copy(group = group) }
            }
        }


    }


}