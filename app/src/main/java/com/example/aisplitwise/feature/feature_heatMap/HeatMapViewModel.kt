package com.example.aisplitwise.feature.feature_heatMap

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.aisplitwise.data.local.Expense
import com.example.aisplitwise.data.local.Group
import com.example.aisplitwise.data.local.Member
import com.example.aisplitwise.data.repository.GroupRepository
import com.example.aisplitwise.data.repository.MemberRepository
import com.example.aisplitwise.feature.feature_ledger.LedgerUIState
import com.example.aisplitwise.navigation.HeatMapRoute
import com.example.aisplitwise.navigation.LedgerRoute
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.heatmaps.WeightedLatLng
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
data class HeatMapUIState(
    val expense: List<WeightedLatLng> =emptyList(),
    val builder: LatLngBounds?=null,


    )

@HiltViewModel
class HeatMapViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository
): ViewModel() {
    val expenseFlow: StateFlow<Expense?> = savedStateHandle.getStateFlow("expenseKey", null)
    private val _uiState = MutableStateFlow(HeatMapUIState())
    val uiState: StateFlow<HeatMapUIState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HeatMapUIState()
    )
    init {

        viewModelScope.launch(Dispatchers.IO){
            viewModelScope.launch(Dispatchers.IO) {
                groupRepository.getExpenseFromGroupId( savedStateHandle.toRoute<HeatMapRoute>().groupId).collect{ expense->
                    val weightedList=expense.map { WeightedLatLng(LatLng(it.latitude, it.longitude))}
                    val latLongList=expense.map {LatLng(it.latitude, it.longitude)  }
                    val builder=LatLngBounds.Builder().apply {
                        latLongList.forEach { include(it) }
                    }.build()
                    _uiState.update { it.copy(expense = weightedList,
                        builder=builder) }

                }
            }
        }
    }

}
