package com.example.aisplitwise.feature.feature_create_group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.aisplitwise.DashBoardRoute
import com.example.aisplitwise.LoginScreenRoute

@Composable
fun  CreateGroupScreen(
    createGroupViewModel: CreateGroupViewModel = hiltViewModel(),
    navController: NavHostController
){
    val uiState=createGroupViewModel.uiState.collectAsState()
    val context= LocalContext.current
    Scaffold(
        Modifier
            .navigationBarsPadding(),
        topBar = {
            CreateGroupHeader{
                navController.popBackStack()
            }
        }) {padding->
        Column (Modifier.padding(padding)){
            var groupName by remember {
                mutableStateOf("")
            }
            var selectedImage by remember {
                mutableStateOf("")
            }
            Text(modifier=Modifier.padding(vertical = 16.dp,horizontal=24.dp),
                text = "Provide a Suitable Name for the Group and Select A Image",
                style = MaterialTheme.typography.titleMedium)
            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Unspecified
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text(modifier=Modifier.padding(vertical = 16.dp,horizontal=24.dp),
                text = "Select a Image that matches vibe of group")

            LazyVerticalGrid(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp), columns = GridCells.Fixed(3)
            ) {
                items(uiState.value.imgList) { item ->
                    val selected by remember(selectedImage) {
                        mutableStateOf(item.compareTo(selectedImage)==0)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .aspectRatio(1f)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedImage = item }
                            .border(
                                width = if (selected) 2.dp else 0.dp, // Add border if selected is true
                                color = if (selected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        SubcomposeAsyncImage(modifier = Modifier
                            .matchParentSize()
                            .align(Alignment.Center)
                            .padding(16.dp),
                            model = ImageRequest.Builder(context)
                                .data(item)
                                .crossfade(true)
                                .build(),
                            contentDescription = "",
                            loading = {
                                Icon(
                                    imageVector = Icons.Default.HourglassBottom, // Replace with your icon resource
                                    contentDescription = "Create Group", // Replace with your string resource
                                    modifier = Modifier.matchParentSize()
                                )
                            },
                            error = {
                                Icon(
                                    imageVector = Icons.Default.BrokenImage, // Replace with your icon resource
                                    contentDescription = "Create Group", // Replace with your string resource
                                    modifier = Modifier.matchParentSize()
                                )
                            })

                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check, // Tick mark icon
                                contentDescription = "Selected",
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.TopEnd)
                                    .background(
                                        MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(4.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { createGroupViewModel.createGroup( groupName=groupName,groupImage=selectedImage,
                    onSuccess = {
                        navController.popBackStack()
                    }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text(text = "Create Group")
            }
        }

    }


}

@Composable
fun CreateGroupHeader(backClick:()->Unit) {
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with your icon resource
                contentDescription = "Create Group", // Replace with your string resource
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
                    .clip(CircleShape)
                    .clickable {
                        backClick.invoke()
                    }
            )
            Text(text = "Create Group",
                style = MaterialTheme.typography.headlineSmall)
        }
    }


}

@Preview
@Composable
fun ImagePreview(){
    val selected by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .aspectRatio(1f)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { }
            .border(
                width = if (selected) 2.dp else 0.dp, // Add border if selected is true
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        SubcomposeAsyncImage(modifier = Modifier
            .matchParentSize()
            .padding(16.dp),
            model = ImageRequest.Builder(context)
                .data("")
                .crossfade(true)
                .build(),
            contentDescription = "",
            loading = {
                Icon(
                    imageVector = Icons.Default.HourglassBottom, // Replace with your icon resource
                    contentDescription = "Create Group", // Replace with your string resource
                    modifier = Modifier.matchParentSize()
                )
            },
            error = {
                Icon(
                    imageVector = Icons.Default.BrokenImage, // Replace with your icon resource
                    contentDescription = "Create Group", // Replace with your string resource
                    modifier = Modifier.matchParentSize()
                )
            })

        if (true) {
            Icon(
                imageVector = Icons.Default.Check, // Tick mark icon
                contentDescription = "Selected",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.secondary) // Align the icon to the top-right
                    , // Add padding to place it within the box
                tint = MaterialTheme.colorScheme.onSecondary // Use theme color for the tick
            )
        }
    }
}