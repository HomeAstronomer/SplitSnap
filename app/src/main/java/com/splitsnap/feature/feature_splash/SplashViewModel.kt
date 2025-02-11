package com.splitsnap.feature.feature_splash

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
data class SplashScreenUiState(
    val isLoading:Boolean=false
)


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(SplashScreenUiState())
    val uiState: StateFlow<SplashScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SplashScreenUiState()
    )
    var isLoggedIn=false
    init {
        viewModelScope.launch (Dispatchers.IO){
            _uiState.update { it.copy(isLoading = true) }
            memberRepository.getMemberDb().collect{member->
                member.getOrNull(0)?.let{ noNullMember ->
                    isLoggedIn=memberRepository.getMemberFromFirestore(noNullMember.uid)!=null
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

}