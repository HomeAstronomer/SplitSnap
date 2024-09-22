package com.example.aisplitwise.feature.feature_expense_dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.data.local.Expense
import com.example.aisplitwise.data.local.Member
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseDialogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    fun addExpense(amount: Int, message: String, toMap: Map<Member, Boolean>, ) {
        viewModelScope.launch (Dispatchers.IO) {
            val expense=Expense(
                id="",
                description = message,
                amount=amount.toDouble(),
                paidBy = Member(),
                splitAmong = toMap.filter { it.value}.keys.toList()
            )
            savedStateHandle["Expense_Key"] = expense
        }
    }
}
