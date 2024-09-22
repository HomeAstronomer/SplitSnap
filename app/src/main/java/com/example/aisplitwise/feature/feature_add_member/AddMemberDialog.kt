package com.example.aisplitwise.feature.feature_add_member

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AddMemberDialog(groupId: String, navHostController: NavHostController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(0.75f))){
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.8f)
                .padding(vertical = 48.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))){
            Column(Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(modifier = Modifier.padding(bottom=8.dp),
                    text="Add a member",
                    style=MaterialTheme.typography.titleMedium)
                Text(modifier = Modifier.padding(bottom=16.dp),
                    text="To add a member share the following link to the user who is using AI SplitWise",
                    style=MaterialTheme.typography.bodyMedium)
                val context = LocalContext.current
                val uri="https://com.example.aisplitwise"

                Text(
                    text = "Share this link",
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "$uri/joinGroup/$groupId")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share link via"))
                        }
                )



            }

        }
    }

}