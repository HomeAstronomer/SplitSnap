package com.splitsnap.feature.feature_joinGroup

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splitsnap.data.local.Group
import com.splitsnap.data.local.Member
import com.splitsnap.data.repository.DataState
import com.splitsnap.data.repository.GroupRepository
import com.splitsnap.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@Immutable
data class JoinGroupUIState(
    val showToast:Boolean=false,
    val toastMessage:String="",
    val showLoader:Boolean=false,

)
@HiltViewModel
class JoinGroupDialogViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinGroupUIState())
    val uiState: StateFlow<JoinGroupUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = JoinGroupUIState()
    )


    fun joinGroup(groupId: String, onSuccess: () -> Unit) {
        viewModelScope.launch (Dispatchers.IO){
            val member=memberRepository.getMemberDb().first().firstOrNull()
            member?.let {memberFromDB->
                groupRepository.joinGroup(member = memberFromDB, groupId = groupId).collect { dataState ->
                    _uiState.update { it.copy(showLoader = false) }
                    when (dataState) {
                        is DataState.Success -> {
                            withContext(Dispatchers.Main) {
                                onSuccess()
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
                        else -> {}
                    }
                }
            }
        }
    }

    fun resetToast() {
        _uiState.update { it.copy(showToast = false, toastMessage ="") }
    }


}