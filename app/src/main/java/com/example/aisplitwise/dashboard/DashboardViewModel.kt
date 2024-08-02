package com.example.aisplitwise.dashboard

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.User
import com.example.aisplitwise.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@Immutable
data class DashboardUiState(
    val box1:Color= Color.Red,
    val box2:Color=Color.Blue,
    val box3:Color=Color.Green,
    val users:List<User> = emptyList()

)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val localDataSource: UserDao,
): ViewModel() {
    val Rand=Random.Default
    fun add() {
        viewModelScope.launch (Dispatchers.IO) {
            localDataSource.insertAll(
                User(
                    uid = Rand.nextInt(),
                    firstName = "Atharva",
                    lastName = "Gorule"
                )
            )
        }

    }
    val state= localDataSource.getAllFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )


    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
        viewModelScope.launch (Dispatchers.IO){
            localDataSource.getAllFlow().collect{data->
              _uiState.update { it.copy(users = data) }

            }
        }
    }
    fun updateColor1(){
        if(_uiState.value.box1== Color.Blue){
            _uiState.update { it.copy(box1 = Color.Red) }
        }else{
            _uiState.update { it.copy(box1 = Color.Blue) }
        }

    }
    fun updateColor2(){
        if(_uiState.value.box2== Color.Green){
            _uiState.update { it.copy(box2 = Color.Cyan) }
        }else{
            _uiState.update { it.copy(box2 = Color.Green) }
        }
    }

}