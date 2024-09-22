package com.example.aisplitwise.feature.feature_joinGroup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.aisplitwise.navigation.AddMemberDialogRoute
import com.example.aisplitwise.navigation.DashBoardRoute
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun JoinGroupDialog(groupId: String, navhostController: NavHostController,
                    joinGroupDialogViewModel: JoinGroupDialogViewModel= hiltViewModel()) {

    val uiState by joinGroupDialogViewModel.uiState.collectAsState()
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
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally){
                var groupIdEnterd by remember{
                    mutableStateOf(groupId)
                }

                Text(modifier=Modifier.padding(bottom=8.dp),
                    text="Join Group",
                    style=MaterialTheme.typography.titleMedium)

                Text(
                    modifier=Modifier.padding(bottom=16.dp),
                    text="Please Enter the Group Id Of the group you want to join",
                    style=MaterialTheme.typography.bodyMedium)

                OutlinedTextField(
                    value = groupIdEnterd,
                    onValueChange = {groupIdEnterd=it },
                    label = { Text("Group ID") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = {
                            joinGroupDialogViewModel.joinGroup(groupIdEnterd) {
                                if (!navhostController.popBackStack(DashBoardRoute, false)) {
                                    navhostController.navigate(DashBoardRoute)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ){
                            Text(text = "Join Group")
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Add Expense",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            navhostController.popBackStack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround) {
                            Text(text = "Cancel")
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }


            }
        }
    }
    if(uiState.showLoader) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))) {
            CircularProgressIndicator(
                Modifier
                    .size(64.dp)
                    .align(Alignment.Center),
                strokeWidth =8.dp )
        }
    }

    val context= LocalContext.current
    LaunchedEffect(key1 = uiState.showToast) {
        if (uiState.showToast) {
            Toast.makeText(
                context,
                uiState.toastMessage,
                Toast.LENGTH_SHORT,
            ).show()
            delay(2.seconds)
            joinGroupDialogViewModel.resetToast()
        }

    }
}