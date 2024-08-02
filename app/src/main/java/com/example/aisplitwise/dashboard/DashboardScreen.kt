package com.example.aisplitwise.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aisplitwise.Profile

@Composable
fun DashBoard(args: Profile, dashBoardViewModel: DashboardViewModel) {
    val uiState by dashBoardViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .background(Color.Cyan)
            .fillMaxSize()
            .navigationBarsPadding()
            .systemBarsPadding()

    ) {
        Column() {
            Box(Modifier.fillMaxWidth().padding(24.dp)) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(color = uiState.box1)
                ) {}
                Box(modifier = Modifier
                    .fillMaxWidth()

                    .height(64.dp)
                    .padding(24.dp)
                    .clickable { }
                    .background(uiState.box2))

            }
            LazyColumn {
                item {
                    Text(text = args.name, Modifier.clickable { dashBoardViewModel.add() })
                }
                item {

                }
                item {
                    Column {
                        Box(
                            Modifier
                                .padding(24.dp)
                                .background(Color.DarkGray)
                                .fillMaxWidth()
                                .height(64.dp)
                                .clickable { dashBoardViewModel.updateColor1() }) {
                        }
                        Box(
                            Modifier
                                .padding(24.dp)
                                .background(Color.Blue)
                                .fillMaxWidth()
                                .height(64.dp)
                                .clickable { dashBoardViewModel.updateColor2() }) {
                        }
                    }

                }
                items(uiState.users) {
                    Column() {
                        Text(text = it.uid.toString(), modifier = Modifier)
                        Text(text = it.firstName.toString(), modifier = Modifier)
                        Text(text = it.lastName.toString(), modifier = Modifier)
                    }
                }
            }
        }

    }
}