package com.example.aisplitwise.feature.feature_signup

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.data.repository.DataState
import com.example.aisplitwise.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SignUpScreenUIState(
    val showToast:Boolean=false,
    val toastMessage:String=""

)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(SignUpScreenUIState())
    val uiState: StateFlow<SignUpScreenUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SignUpScreenUIState()
    )

    fun signup(
        email: String,
        password: String,
        displayName:String,
        phoneNumber:String,
        onSuccess: () -> Unit,
    ) {


        viewModelScope.launch {
            memberRepository.signup(email, password, displayName, phoneNumber).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        onSuccess.invoke()
                    }

                    is DataState.Error -> {
                        _uiState.update {
                            it.copy(
                                showToast = true,
                                toastMessage = dataState.errorMessage
                            )
                        }
                    }
                }
            }


        }
    }

    fun resetToast() {
        _uiState.update { it.copy(showToast = false, toastMessage ="") }
    }

}




