package com.splitsnap.feature.feature_Profile

import android.hardware.lights.Light
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.splitsnap.data.local.Member
import com.splitsnap.theme.SplitSnapTheme
import com.splitsnap.theme.SplitSnapThemeEnum
import com.splitsnap.utils.ifNullOrEmpty

@Composable
fun ProfileScreen(viewModel: ProfileViewModel= hiltViewModel()){
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()
    val context= LocalContext.current
    Scaffold(topBar ={Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))}) {
        ProfileScreenUI(Modifier.padding(it),uiState.value.member,
            selectedTheme = SplitSnapThemeEnum.LIGHT,
            onThemeSelected = {}
            )
    }

}

@Composable
fun ProfileScreenUI(modifier: Modifier = Modifier, member: Member,selectedTheme: SplitSnapThemeEnum,onThemeSelected:(SplitSnapThemeEnum)->Unit) {
    LazyColumn(modifier=modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceBright)) {
        item{
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(24.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                val context = LocalContext.current
                Column(Modifier.weight(1f)) {
                    Text(
                        text = member?.displayName ?: "No Name",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = member?.email ?: "No Email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = member?.phoneNumber?.ifNullOrEmpty { "No Phone Number" }
                            ?: "No Phone Number",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(context).data(member?.photoUrl ?: "")
                        .crossfade(true)

                        .build(),
                    contentDescription = "",
                    loading = {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle, // Replace with your icon resource
                            contentDescription = "Create Group", // Replace with your string resource
                            modifier = Modifier.size(64.dp),
                            tint =  MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    error = {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle, // Replace with your icon resource
                            contentDescription = "Create Group", // Replace with your string resource
                            modifier = Modifier.size(64.dp),
                            tint =  MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    contentScale = ContentScale.FillBounds
                )

                
            }
        }
        item{
            val isExpanded = remember { mutableStateOf(false) }
            Column() {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top=16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette, // Replace with your icon resource
                        contentDescription = "Create Group", // Replace with your string resource
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Set Theme",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choose between Dark,Light or System Default",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }


                    val rotation = animateFloatAsState(
                        targetValue = if (isExpanded.value) {
                            0f
                        } else {
                            180f
                        },
                        label = ""
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown, // Replace with your icon resource
                        contentDescription = "Create Group", // Replace with your string resource
                        modifier = Modifier.size(24.dp).rotate(rotation.value).clickable{isExpanded.value=!isExpanded.value},
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )


                }
                AnimatedVisibility(isExpanded.value) {
                    Column(Modifier.padding(start = 48.dp,top=8.dp)) {

                        SplitSnapThemeEnum.entries.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onThemeSelected(option) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (option == selectedTheme),
                                    onClick = { onThemeSelected(option) }
                                )
                                Text(
                                    text = option.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

        }
    }

}


@Preview
@Composable
fun ProfilePreView(){
    val previewMember = Member(
        uid = "12345",
        displayName = "John Doe",
        email = "john.doe@example.com",
        phoneNumber = "+1234567890",
        photoUrl = "https://example.com/profile.jpg",
        createdGroupIds = listOf("group1", "group2"),
        joinedGroupIds = listOf("group3", "group4", "group5")
    )
    SplitSnapTheme {
        ProfileScreenUI(modifier = Modifier, member = previewMember, SplitSnapThemeEnum.LIGHT,{})
    }
}