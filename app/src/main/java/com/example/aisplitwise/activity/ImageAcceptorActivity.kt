package com.example.aisplitwise.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aisplitwise.feature.feature_Image_Acceptor.ImageAcceptorScreen
import com.example.aisplitwise.ui.theme.AISplitwiseTheme
import com.google.firebase.vertexai.GenerativeModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageAcceptorActivity : ComponentActivity() {

    @Inject lateinit var generativeModel: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val imageUri =  intent.extras?.get(Intent.EXTRA_STREAM).toString()



        setContent {
            AISplitwiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                     ImageAcceptorScreen(imageUri,
                         imageAcceptorViewModel = hiltViewModel()
                     )
                }
            }
        }
    }
}
