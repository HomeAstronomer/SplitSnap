package com.example.aisplitwise.feature.feature_login

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.User
import com.example.aisplitwise.UserDao
import com.example.aisplitwise.feature.dashboard.DashboardUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@Immutable
data class LoginScreenUiState(
    val emailId:String="",
    val password:String=""

)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val localDataSource: UserDao,
    private val fireStoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    fun emailLogin(email:String,password:String,
                   onSuccess:(FirebaseUser?)->Unit,
                   onError:(String?)->Unit) {
        viewModelScope.launch {

            val signInTask = firebaseAuth.createUserWithEmailAndPassword(email, password)
            signInTask.addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   onSuccess.invoke(task.result.user)
                } else {
                    onError.invoke(task.exception?.message)

                }
            }

            }

    }

    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginScreenUiState()
    )

}