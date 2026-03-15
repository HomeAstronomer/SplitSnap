package com.splitsnap.feature.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.splitsnap.data.local.Group
import com.splitsnap.data.local.Member
import com.splitsnap.navigation.CreateGroupRoute
import com.splitsnap.navigation.JoinGroupDialogRoute
import com.splitsnap.navigation.LedgerRoute
import com.splitsnap.navigation.LoginScreenRoute
import com.splitsnap.utils.ifNullOrEmpty
import com.google.firebase.Timestamp
import java.util.Date


@Composable
fun DashBoard(dashBoardViewModel: DashboardViewModel, navController: NavHostController) {
    val uiState by dashBoardViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            DashboardHeader(uiState.member,
                createGroup = { navController.navigate(CreateGroupRoute) },
                onLogout = {
                    dashBoardViewModel.logout {
                        navController.navigate(LoginScreenRoute) {
                            popUpTo(0)
                        }
                    }
                },
                navController = navController
            )
        },
    ) { padding ->
        DashBoardContent(
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            dashBoardViewModel::getGroupsApiCall,
            uiState.groupList,
            navigateGroup = { navController.navigate(LedgerRoute(it)) },
        )


    }
    if (uiState.showLoader) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
        ) {
            CircularProgressIndicator(
                Modifier
                    .size(64.dp)
                    .align(Alignment.Center), strokeWidth = 8.dp
            )
        }
    }

}

@Composable
fun DashBoardContent(
    modifier: Modifier = Modifier,
    getGroupsApiCall: () -> Unit,
    groupList: List<Group> = emptyList(),
    navigateGroup: (String) -> Unit,
) {
    val context = LocalContext.current
    var showApplinkDialog by remember { mutableStateOf(false) }
    var isLinkDialogShown by rememberSaveable { mutableStateOf(false) }
    var isApplinkingEnabled by remember{mutableStateOf(true)}

    val lifecycleOwner = LocalLifecycleOwner.current

    fun checkAppLinks() {
        if (Build.VERSION.SDK_INT >= 31) {

            val manager = context.getSystemService(DomainVerificationManager::class.java)
            val userState = manager.getDomainVerificationUserState(context.packageName)

            val enabledDomains = userState?.hostToStateMap?.filterValues {
                it == DomainVerificationUserState.DOMAIN_STATE_VERIFIED ||
                        it == DomainVerificationUserState.DOMAIN_STATE_SELECTED
            }

            isApplinkingEnabled = enabledDomains?.isNotEmpty() == true

            if (!isApplinkingEnabled && !isLinkDialogShown) {
                showApplinkDialog = true
                isLinkDialogShown = true
            }
        }
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkAppLinks()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier) {
        Column(Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Row(
                        Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Groups",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        Icon(imageVector = Icons.Rounded.Replay,
                            contentDescription = "Refresh Group",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    getGroupsApiCall.invoke()
                                })
                    }


                }

                items(groupList) {
                    GroupCard(it) {
                        navigateGroup.invoke(it.id)
                    }
                }

            }

//            Text(
//                text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
        }
        AnimatedVisibility (!isApplinkingEnabled,modifier=Modifier.align (Alignment.BottomCenter)) {
            TextButton(onClick = {
                showApplinkDialog = true
            }) { Text("App Linking Not Enabled") }
        }
    }


    AnimatedVisibility(showApplinkDialog) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.SettingsSuggest, contentDescription = "Example Icon")
            },
            title = {
                Text(text = "Enable Link Opening by Default")
            },
            text = {
                Text(text = "To provide a seamless experience, please allow this app to open links automatically from your system settings. After that, click on 'Add Link' to set the default")
            },
            onDismissRequest = {
                showApplinkDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)

                        showApplinkDialog = false
                    }
                ) {
                    Text("Go to Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showApplinkDialog = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

}


@Composable
fun DashboardHeader(
    route: Member?, createGroup: () -> Unit, onLogout: () -> Unit, navController: NavHostController
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),

        ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                val context = LocalContext.current

                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(context).data(route?.photoUrl ?: "")
                        .crossfade(true)

                        .build(),
                    contentDescription = "",
                    loading = {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "User Avatar",
                            modifier = Modifier.size(64.dp)
                        )
                    },
                    error = {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "User Avatar",
                            modifier = Modifier.size(64.dp)
                        )
                    },
                    contentScale = ContentScale.FillBounds
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route?.displayName ?: "No Name",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = route?.email ?: "No Email",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = route?.phoneNumber?.ifNullOrEmpty { "No Phone Number" }
                            ?: "No Phone Number",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        createGroup()
                    }, modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "Create Group")
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
                        navController.navigate(JoinGroupDialogRoute(""))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "Add Member")
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

@Preview(showBackground = true)
@Composable
fun DashboardHeaderPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
    ) {
        CircularProgressIndicator(
            Modifier
                .size(64.dp)
                .align(Alignment.Center), strokeWidth = 8.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardContentPreview() {
    Surface(color = Color.White) {

        DashBoardContent(Modifier, getGroupsApiCall = {}, groupList = listOf(
            Group(
                id = "group1",
                name = "Weekend Getaway",
                members = emptyList(),
                createdAt = Timestamp(Date()),
                updatedAt = Timestamp(Date()),
                groupImg = "https://example.com/sample-group-img.jpg"
            )
        ), navigateGroup = {}
        )
    }
}
