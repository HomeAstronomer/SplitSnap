package com.splitsnap.feature.feature_login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splitsnap.data.repository.DataState
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


@Immutable
data class LoginScreenUiState(
    var showToast:Boolean=false,
    var toastMessage:String="",
    var showLoader:Boolean=false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginScreenUiState()
    )

    fun signIn(
        email: String, password: String, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(showLoader = true )
            }
            memberRepository.firebaseAuthSignIn(email, password).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        onSuccess.invoke()
                    }

                    is DataState.Error -> {
                        _uiState.update {
                            it.copy (
                                showToast = true,
                                toastMessage = dataState.errorMessage,
                                showLoader = false
                            )
                        }
                    }
                }
            }


        }

    }

    fun resetToast() {
        _uiState.update {
            it.apply {
                showToast = false
                toastMessage = ""
            }
        }
    }

    fun firebaseAuthWithGoogle(
        idToken: String, onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy ( showLoader = true ) }
            }
            memberRepository.loginUsingGoogle(idToken).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        withContext(Dispatchers.Main) {
                            onSuccess.invoke()
                        }
                    }

                    is DataState.Error -> {
                        _uiState.update {
                            it.copy (
                                showToast = true,
                                toastMessage = dataState.errorMessage,
                                showLoader = false
                            )
                        }
                    }
                }

            }
        }

    }


}