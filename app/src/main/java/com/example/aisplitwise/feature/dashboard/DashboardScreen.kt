package com.example.aisplitwise.feature.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.aisplitwise.DashBoardRoute
import com.example.aisplitwise.data.local.Group
import com.example.aisplitwise.data.local.Member
import com.example.aisplitwise.utils.ifNullOrEmpty
import com.google.firebase.Timestamp
import java.util.Date


@Composable
fun DashBoard(navArgs: DashBoardRoute, dashBoardViewModel: DashboardViewModel) {
    val uiState by dashBoardViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier=Modifier,topBar = { DashboardHeader(navArgs) }) { padding ->
        DashBoardContent(
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding(),
            navArgs,
            dashBoardViewModel::createGroup,
            dashBoardViewModel::getNewgroupId,
            uiState.groupList,
            dashBoardViewModel::getListOfMemberGroups,
            dashBoardViewModel::getGroups
        )

    }

}

@Composable
fun DashBoardContent(
    modifier: Modifier = Modifier,
    navArgs: DashBoardRoute,
    createGroup: (Group, () -> Unit, (String) -> Unit, String) -> Unit,
    getNewgroupId: () -> String,
    groupList: List<Group> = emptyList(),
    getListOfMemberGroups: ((Member) -> Unit) -> Unit,
    getGroups: (Member) -> Unit
) {
    val context = LocalContext.current
    Box(modifier = modifier) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Groups",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Button(onClick = {
                    val group = Group(
                        id = getNewgroupId(),
                        name = "The Boys",
                        members = listOf(
                            Member(
                                uid = navArgs.uid,
                                displayName = navArgs.displayName,
                                email = navArgs.email,
                                phoneNumber = navArgs.phoneNumber,
                                photoUrl = navArgs.photoUrl,
                                createdGroupIds = emptyList(),
                                joinedGroupIds = emptyList()
                            )
                        ),
                        createdAt = Timestamp(Date()),
                        updatedAt = Timestamp(Date()),
                        expenses = emptyList()
                    )
                    createGroup(group,
                        {
                            Toast.makeText(
                                context, "Group Created Successfully ",
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                        { error ->
                            Toast.makeText(
                                context, error,
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                        navArgs.uid
                    )
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Add, // Replace with your icon resource
                        contentDescription = "Create Group", // Replace with your string resource
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Group", // Replace with your string resource
                        style = MaterialTheme.typography.bodyLarge,
                    )

                }

                Icon(
                    imageVector = Icons.Rounded.Replay, // Replace with your icon resource
                    contentDescription = "Create Group", // Replace with your string resource
                    modifier = Modifier.size(24.dp).clickable {
                        getListOfMemberGroups {member->
                            getGroups(member)
                        }
                    }
                )




            }

            LazyColumn {
                items(groupList){it->
                    GroupCard(it){}

                }

            }


        }
    }

}


@Composable
fun DashboardHeader(
    route: DashBoardRoute
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context= LocalContext.current

            SubcomposeAsyncImage(model = ImageRequest.Builder(context)
                .data(route.photoUrl ?: "")
                .crossfade(true)
                .build(), contentDescription = "",
                loading = {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle, // Replace with your icon resource
                        contentDescription ="Create Group", // Replace with your string resource
                        modifier = Modifier.size(64.dp)
                    )
                },
                error = { Icon(
                    imageVector = Icons.Rounded.AccountCircle, // Replace with your icon resource
                    contentDescription ="Create Group", // Replace with your string resource
                    modifier = Modifier.size(64.dp)
                )})

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = route.displayName ?: "No Name",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = route.email ?: "No Email",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =route.phoneNumber?.ifNullOrEmpty { "No Phone Number" }?:"No Phone Number",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardHeaderPreview() {
    Surface(color = Color.White) {
        DashboardHeader(
            route = DashBoardRoute(
                displayName = "John Doe",
                email = "john.doe@example.com",
                phoneNumber = "+1234567890",
                photoUrl = "https://example.com/photo.jpg"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardContentPreview() {
    Surface(color = Color.White) {

        DashBoardContent(
            Modifier,
            navArgs = DashBoardRoute(
                displayName = "John Doe",
                email = "john.doe@example.com",
                phoneNumber = "+1234567890",
                photoUrl = "https://example.com/photo.jpg"
            ),
            createGroup = {_,_,_,_->},
            getNewgroupId = {""},
            listOf(
                Group(
                    id = "group1",
             name = "Weekend Getaway",
             members = emptyList(),
             expenses = listOf(), // Add dummy expenses if needed
             createdAt = Timestamp(Date()),
             updatedAt = Timestamp(Date()),
             groupImg = "https://example.com/sample-group-img.jpg" // Replace with a valid image URL
         )
            ),
            {},
            {}
        )
    }
}
