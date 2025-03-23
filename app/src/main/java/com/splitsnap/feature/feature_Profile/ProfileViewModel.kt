package com.splitsnap.feature.feature_Profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splitsnap.data.local.Member
import com.splitsnap.data.repository.MemberRepository
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
data class ProfileScreenUiState(
    val showToast:Boolean=false,
    val toastMessage:String="",
    val showLoader:Boolean=false,
    val member:Member= Member()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {



    private val _uiState = MutableStateFlow(ProfileScreenUiState())
    val uiState: StateFlow<ProfileScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileScreenUiState()

    )
    init{
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
}