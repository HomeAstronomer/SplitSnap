package com.example.aisplitwise.feature.feature_ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.aisplitwise.uiCore.atoms.ImageCompose

@Composable
fun LedgerScreen(navHostController: NavHostController,
                 ledgerViewModel:LedgerViewModel = hiltViewModel()){
    val uiState by ledgerViewModel.uiState.collectAsState()
    Scaffold(topBar = {LedgerScreenHeader { navHostController.popBackStack() } }) {padding->
        uiState.group?.let {group->
            Row(Modifier.padding(padding).background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically) {
                ImageCompose(
                    Modifier
                        .size(96.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .padding(16.dp)
                       ,
                    data = group.groupImg
                )
                Column(Modifier.padding(start=16.dp).weight(1f)) {
                    Text(
                        text = "Group Name",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Icon(
                    imageVector = Icons.Default.Add, // Replace with your icon resource
                    contentDescription = "Create Group", // Replace with your string resource
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                        .clip(CircleShape)
                )


            }
        }
    }
}

@Composable
fun LedgerScreenHeader(backClick:()->Unit) {
    Box(modifier = Modifier
        .background(MaterialTheme.colorScheme.primaryContainer)
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 24.dp, vertical = 8.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Click",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
                    .clip(CircleShape)
                    .clickable {
                        backClick.invoke()
                    }
            )
            Text(text = "Expenses",
                style = MaterialTheme.typography.headlineSmall)
        }
    }


}
