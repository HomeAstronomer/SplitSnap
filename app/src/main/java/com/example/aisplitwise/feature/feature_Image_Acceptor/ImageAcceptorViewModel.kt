package com.example.aisplitwise.feature.feature_Image_Acceptor

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.feature.dashboard.DashboardUiState
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.content
import com.google.gson.Gson
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

data class TransactionDetails(
    val receiver: Receiver,
    val amount: Double,
    val time: String, // ISO 8601 format
    val transactionId: String,
    val platform: String
)

data class Receiver(
    val name: String,
    val upiId: String
)


@Immutable
data class ImageAcceptorUiState(
    var transactionDetails: TransactionDetails?=null
)

@HiltViewModel
class ImageAcceptorViewModel @Inject constructor(
    private var generativeModel: GenerativeModel
) : ViewModel()  {
    private val _uiState = MutableStateFlow(ImageAcceptorUiState())
    val uiState: StateFlow<ImageAcceptorUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImageAcceptorUiState()
    )

    suspend fun detectImageData(bitmap: Bitmap):String{
         val prompt = content {
             image(bitmap)
             text("Extract Reciever Name ,Reciever UPI Id,Amount ,TransactionId,Time in iso")
         }
        val recognisedText=generativeModel.generateContent(prompt).text ?: ""
        val gson = Gson()
        val transactionDetails = gson.fromJson(recognisedText, TransactionDetails::class.java)
        _uiState.update {
            it.copy(transactionDetails=transactionDetails)
        }
        return recognisedText
    }

    fun setTransactionModel(transactionModel:TransactionDetails){
        _uiState.update {
            it.copy(transactionDetails =transactionModel )
        }
    }
}